package mb.statix.search;

import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

/**
 * A focused search state.
 *
 * @param <C> the type of constraint to focus on
 */
public final class FocusedSearchState<C extends IConstraint> {

    private final SearchState searchState;

    private final C focus;
    private final Set.Immutable<IConstraint> unfocused;

    public FocusedSearchState(SearchState searchState, C focus) {
        this.searchState = searchState;
        Set.Immutable<IConstraint> constraints = searchState.getConstraints();
        if(!constraints.contains(focus)) {
            throw new IllegalArgumentException("The focus constraint is not one of the constraints in the state.");
        }
        this.focus = focus;
        this.unfocused = constraints.__remove(focus);
    }

    public SearchState getSearchState() {
        return searchState;
    }

    public C getFocus() {
        return focus;
    }

    public Set<IConstraint> getUnfocused() {
        return unfocused;
    }

}
