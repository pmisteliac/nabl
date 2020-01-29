package mb.statix.search;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.persistent.SolverResult;
import mb.statix.spec.ApplyResult;

import javax.annotation.Nullable;


/**
 * The state of the searcher.
 */
public final class SearchState {

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

    @Nullable public ImmutableMap<ITermVar, ITermVar> getExistentials() {
        return existentials != null ? existentials : ImmutableMap.of();
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

}
