package mb.statix.search.strategies;

import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Strategy;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;

import java.util.stream.Stream;


/**
 * Search strategy that only succeeds if the search state has no errors.
 */
public final class IsSuccessfulStrategy implements Strategy<SearchState, SearchState, SearchContext> {

    @Override
    public Stream<SearchState> apply(SearchContext ctx, SearchState state) throws InterruptedException {
        if (state.hasErrors()) {
            return Stream.empty();
        } else {
            return Stream.of(state);
        }
    }

    @Override
    public String toString() {
        return "isSuccessful";
    }

}
