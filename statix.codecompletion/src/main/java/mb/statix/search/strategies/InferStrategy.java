package mb.statix.search.strategies;

import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Sequence;
import mb.statix.search.Strategy;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;


/**
 * Performs inference on the search state.
 */
public final class InferStrategy implements Strategy<SearchState, SearchState, SearchContext> {

    @Override
    public Sequence<SearchState> apply(SearchContext ctx, SearchState state) throws InterruptedException {

        final SolverResult result = Solver.solve(
                ctx.getSpec(),
                state.getState(),
                state.getConstraints(),
                state.getDelays(),
                state.getCompleteness(),
                new NullDebugContext()
        );

        if (result.hasErrors()) {
            return Sequence.empty();
        } else {
            return Sequence.of(SearchState.fromSolverResult(result, state.getExistentials()));
        }
    }

    @Override
    public String toString() {
        return "infer";
    }

}
