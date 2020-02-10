package mb.statix.search.strategies;

import mb.statix.search.FocusedSearchState;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Strategy;
import mb.statix.solver.IConstraint;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Unfocuses any constraint.
 */
public final class UnfocusStrategy<C extends IConstraint> implements Strategy<FocusedSearchState<C>, SearchState, SearchContext> {

    @Override
    public Stream<SearchState> apply(SearchContext searchContext, FocusedSearchState<C> input) throws InterruptedException {
        return Stream.of(input.getSearchState());

    }

    @Override
    public String toString() {
        return "unfocus";
    }

}
