package mb.statix.search;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITermVar;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;


/**
 * The state of the searcher.
 */
public final class SearchState {

    protected final IState.Immutable state;
    protected final Set.Immutable<IConstraint> constraints;
    protected final Map.Immutable<IConstraint, Delay> delays;
    protected final ImmutableMap<ITermVar, ITermVar> existentials;
    protected final ICompleteness.Immutable completeness;

    protected SearchState(IState.Immutable state, Set.Immutable<IConstraint> constraints,
                          Map.Immutable<IConstraint, Delay> delays, ImmutableMap<ITermVar, ITermVar> existentials,
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

    public Set.Immutable<IConstraint> getConstraints() {
        return constraints;
    }

    public Map.Immutable<IConstraint, Delay> getDelays() {
        return delays;
    }

    public Iterable<IConstraint> getConstraintsAndDelays() {
        return Iterables.concat(constraints, delays.keySet());
    }

    public ImmutableMap<ITermVar, ITermVar> getExistentials() {
        return existentials != null ? existentials : ImmutableMap.of();
    }

    public ICompleteness.Immutable getCompleteness() {
        return completeness;
    }

}
