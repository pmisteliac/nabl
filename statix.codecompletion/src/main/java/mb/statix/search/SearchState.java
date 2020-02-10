package mb.statix.search;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.nabl2.terms.unification.Unifiers;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.nabl2.util.CapsuleUtil;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.Completeness;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.persistent.SolverResult;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Spec;
import org.metaborg.util.functions.Action1;
import org.metaborg.util.functions.Function2;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import javax.annotation.Nullable;


/**
 * The state of the searcher.
 */
public final class SearchState {

    private final static ILogger log = LoggerUtils.logger(SearchState.class);

    /**
     * Creates a new {@link SearchState} from the given specification, solver state, and constraints.
     *
     * @param spec the Statix specification
     * @param state the solver state
     * @param constraints the constraints
     * @return the resulting search state
     */
    public static SearchState of(Spec spec, IState.Immutable state, Iterable<? extends IConstraint> constraints) {
        final ICompleteness.Transient completeness = Completeness.Transient.of(spec);
        completeness.addAll(constraints, state.unifier());
        return new SearchState(state, CapsuleUtil.toSet(constraints), Map.Immutable.of(),
                null, completeness.freeze());
    }

    /**
     * Creates a new {@link SearchState} from the given solver result.
     *
     * @param result the result of inference by the solver
     * @param existentials
     * @return the resulting search state
     */
    public static SearchState fromSolverResult(SolverResult result, @Nullable ImmutableMap<ITermVar, ITermVar> existentials) {
        final Set.Transient<IConstraint> constraints = Set.Transient.of();
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        result.delays().forEach((c, d) -> {
            if(d.criticalEdges().isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });

        final ImmutableMap<ITermVar, ITermVar> newExistentials =
                existentials == null ? result.existentials() : existentials;
        return new SearchState(result.state(), constraints.freeze(), delays.freeze(), newExistentials,
                result.completeness());
    }

    protected final IState.Immutable state;
    protected final Set.Immutable<IConstraint> constraints;
    protected final Map.Immutable<IConstraint, Delay> delays;
    @Nullable protected final ImmutableMap<ITermVar, ITermVar> existentials;
    protected final ICompleteness.Immutable completeness;

    protected SearchState(IState.Immutable state,
                          Set.Immutable<IConstraint> constraints,
                          Map.Immutable<IConstraint, Delay> delays,
                          @Nullable ImmutableMap<ITermVar, ITermVar> existentials,
                          ICompleteness.Immutable completeness) {
        this.state = state;
        this.constraints = constraints;
        this.delays = delays;
        this.existentials = existentials;
        this.completeness = completeness;
    }

    /** Gets the solver state. */
    public IState.Immutable getState() {
        return state;
    }

    /** The constraints left to solve. */
    public Set.Immutable<IConstraint> getConstraints() {
        return constraints;
    }

    /** The constraints that have been delayed due to critical edges. */
    public Map.Immutable<IConstraint, Delay> getDelays() {
        return delays;
    }

//    public Iterable<IConstraint> getConstraintsAndDelays() {
//        return Iterables.concat(constraints, delays.keySet());
//    }

    /**
     * The variables that have been existentially quantified in the most top-level constraint;
     * or {@code null} when no constraints have existentially quantified any variables (yet).
     *
     * This is used to be able to find the value assigned to the top-most quantified variables.
     */
    @Nullable public ImmutableMap<ITermVar, ITermVar> getExistentials() {
        return this.existentials;
    }

    public ICompleteness.Immutable getCompleteness() {
        return completeness;
    }

    /**
     * Updates this search state with the specified {@link ApplyResult} and returns the new state.
     *
     * @param result the {@link ApplyResult}
     * @param focus the focus constraint
     * @return the updated search state
     */
    public SearchState withApplyResult(ApplyResult result, IConstraint focus) {
        final IConstraint applyConstraint = result.body();
        final IState.Immutable applyState = result.state();
        final IUniDisunifier.Immutable applyUnifier = applyState.unifier();

        // Update constraints
        final Set.Transient<IConstraint> constraints = this.getConstraints().asTransient();
        constraints.__insert(applyConstraint);
        constraints.__remove(focus);

        // Update completeness
        final ICompleteness.Transient completeness = this.getCompleteness().melt();
        completeness.updateAll(result.diff().varSet(), applyUnifier);
        completeness.add(applyConstraint, applyUnifier);
        java.util.Set<CriticalEdge> removedEdges = completeness.remove(focus, applyUnifier);

        // Update delays
        final io.usethesource.capsule.Map.Transient<IConstraint, Delay> delays = io.usethesource.capsule.Map.Transient.of();
        this.getDelays().forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });

        return new SearchState(applyState, constraints.freeze(), delays.freeze(), this.getExistentials(), completeness.freeze());
    }

    /**
     * Update the constraints, keeping completeness and delayed constraints in sync.
     *
     * This method assumes that no constraints appear in both add and remove, or it will be incorrect!
     *
     * @param add the constraints to add
     * @param remove the constraints to remove
     * @return the new search state
     */
    public SearchState updateConstraints(Iterable<IConstraint> add, Iterable<IConstraint> remove) {

        final ICompleteness.Transient completeness = this.completeness.melt();
        final Set.Transient<IConstraint> constraints = this.constraints.asTransient();
        final java.util.Set<CriticalEdge> removedEdges = Sets.newHashSet();
        add.forEach(c -> {
            if(constraints.__insert(c)) {
                completeness.add(c, state.unifier());
            }
        });
        remove.forEach(c -> {
            if(constraints.__remove(c)) {
                removedEdges.addAll(completeness.remove(c, state.unifier()));
            }
        });
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        this.delays.forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });
        return new SearchState(state, constraints.freeze(), delays.freeze(), existentials, completeness.freeze());
    }

    public SearchState delay(Iterable<? extends java.util.Map.Entry<IConstraint, Delay>> delay) {
        final Set.Transient<IConstraint> constraints = this.constraints.asTransient();
        final Map.Transient<IConstraint, Delay> delays = this.delays.asTransient();
        delay.forEach(entry -> {
            if(constraints.__remove(entry.getKey())) {
                delays.__put(entry.getKey(), entry.getValue());
            } else {
                log.warn("delayed constraint not in constraint set: {}", entry.getKey());
            }
        });
        return new SearchState(state, constraints.freeze(), delays.freeze(), existentials, completeness);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        print(ln -> {
            sb.append(ln).append("\n");
        }, (t, u) -> new UnifierFormatter(u, 2).format(t));
        return sb.toString();
    }

    private void print(Action1<String> printLn, Function2<ITerm, IUniDisunifier, String> pp) {
        final IUniDisunifier unifier = state.unifier();
        printLn.apply("SearchState");
        printLn.apply("| vars:");
        for(Map.Entry<ITermVar, ITermVar> existential : existentials.entrySet()) {
            String var = pp.apply(existential.getKey(), Unifiers.Immutable.of());
            String term = pp.apply(existential.getValue(), unifier);
            printLn.apply("|   " + var + " : " + term);
        }
        printLn.apply("| unifier: " + state.unifier().toString());
        printLn.apply("| completeness: " + completeness.toString());
        printLn.apply("| constraints:");
        for(IConstraint c : constraints) {
            printLn.apply("|   " + c.toString(t -> pp.apply(t, unifier)));
        }
        printLn.apply("| delays:");
        for(java.util.Map.Entry<IConstraint, Delay> e : delays.entrySet()) {
            printLn.apply("|   " + e.getValue() + " : " + e.getKey().toString(t -> pp.apply(t, unifier)));
        }
    }


}
