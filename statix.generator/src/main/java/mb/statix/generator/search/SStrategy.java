package mb.statix.generator.search;

/**
 * A search strategy, which takes a number of search nodes and produces a number of search nodes.
 * When the produced set of nodes is empty, the strategy failed.
 */
public interface SStrategy {

    /**
     * Applies the search strategy to the given result.
     *
     * @param context the context
     * @param input the input
     * @return the output
     */
    StrategyNode apply(StrategyContext context, StrategyNode input);

}
