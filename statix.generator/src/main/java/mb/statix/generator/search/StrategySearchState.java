package mb.statix.generator.search;

import com.google.common.collect.ImmutableMap;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITermVar;
import mb.statix.generator.SearchState;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;


/**
 * A search strategy state.
 */
public final class StrategySearchState extends SearchState {

    private final Set.Immutable<IConstraint> focusedConstraints;
    private final Set.Immutable<IConstraint> unfocusedConstraints;

    protected StrategySearchState(
            IState.Immutable state,
            Set.Immutable<IConstraint> constraints,
            Set.Immutable<IConstraint> focusedConstraints,
            Map.Immutable<IConstraint, Delay> delays,
            ImmutableMap<ITermVar, ITermVar> existentials,
            ICompleteness.Immutable completeness
    ) {
        super(state, constraints, delays, existentials, completeness);

        if (!constraints.containsAll(focusedConstraints)) {
            throw new IllegalArgumentException("The focused constraints must be a subset of the constraints in the state.");
        }
        this.focusedConstraints = focusedConstraints;
        this.unfocusedConstraints = constraints.__removeAll(focusedConstraints);
    }

    /**
     * Gets the focused constraints.
     *
     * @return the set of focused constraints
     */
    public Set.Immutable<IConstraint> getFocusedConstraints() {
        return this.focusedConstraints;
    }

    /**
     * Gets the unfocused constraints.
     *
     * @return the set of unfocused constraints
     */
    public Set.Immutable<IConstraint> getUnfocusedConstraints() {
        return this.unfocusedConstraints;
    }

}
