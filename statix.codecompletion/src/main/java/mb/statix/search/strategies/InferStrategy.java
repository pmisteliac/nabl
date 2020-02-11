package mb.statix.search.strategies;

import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Strategy;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;

import java.util.stream.Stream;


/**
 * Performs inference on the search state.
 *
 * NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.
 */
public final class InferStrategy implements Strategy<SearchState, SearchState, SearchContext> {

    @Override
    public Stream<SearchState> apply(SearchContext ctx, SearchState state) throws InterruptedException {

        final SolverResult result = Solver.solve(
                ctx.getSpec(),
                state.getState(),
                state.getConstraints(),
                state.getDelays(),
                state.getCompleteness(),
                new NullDebugContext()
        );

        // NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.

        return Stream.of(SearchState.fromSolverResult(result, state.getExistentials()));
    }

    @Override
    public String toString() {
        return "infer";
    }

}
