package mb.statix.generator.search;


import mb.statix.constraints.Constraints;
import mb.statix.generator.SearchState;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;

import java.util.stream.Stream;


/**
 * Inference strategy.
 */
public final class InferStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link InferStrategy} class.
     */
    public InferStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        return StrategyNode.of(input.getStates().flatMap(ss -> {
            final SolverResult resultConfig;
            try {
                resultConfig = Solver.solve(context.getSpec(), ss.state(), ss.constraints(), ss.delays(),
                        ss.completeness(), new NullDebugContext());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (resultConfig.hasErrors()) {
//                final String msg = Constraints.toString(resultConfig.messages().keySet(), resultConfig.state().unifier()::toString);
                return Stream.empty();
            }
            return Stream.of(StrategySearchState.of(ss.replace(resultConfig)));
        }));
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "infer";
    }

}
