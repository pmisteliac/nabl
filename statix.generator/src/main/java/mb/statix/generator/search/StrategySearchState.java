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

import javax.annotation.Nullable;


/**
 * A search strategy state.
 */
public final class StrategySearchState extends SearchState {

    @Nullable private final IConstraint focusedConstraint;
    private final Set.Immutable<IConstraint> unfocusedConstraints;

    protected StrategySearchState(
            IState.Immutable state,
            Set.Immutable<IConstraint> constraints,
            @Nullable IConstraint focusedConstraint,
            Map.Immutable<IConstraint, Delay> delays,
            ImmutableMap<ITermVar, ITermVar> existentials,
            ICompleteness.Immutable completeness
    ) {
        super(state, constraints, delays, existentials, completeness);

        if (!constraints.contains(focusedConstraint)) {
            throw new IllegalArgumentException("The focused constraint must be a subset of the constraints in the state.");
        }
        this.focusedConstraint = focusedConstraint;
        this.unfocusedConstraints = constraints.__remove(focusedConstraint);
    }

    /**
     * Gets the focused constraint.
     *
     * @return the focused constraint; or {@code null} when none is focused
     */
    @Nullable
    public IConstraint getFocusedConstraint() {
        return this.focusedConstraint;
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
