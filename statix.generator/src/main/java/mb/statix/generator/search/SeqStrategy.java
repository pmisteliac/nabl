package mb.statix.generator.search;


import java.util.List;


/**
 * Applies one strategy, then another.
 */
public final class SeqStrategy implements SStrategy {

    private final List<SStrategy> strategies;

    /**
     * Initializes a new instance of the {@link SeqStrategy} class.
     *
     * @param strategies the strategies to apply
     */
    public SeqStrategy(List<SStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // s1 ; s2 ; ..
        StrategyNode result = input;
        for (SStrategy s : this.strategies) {
            result = s.apply(context, result);
        }
        return result;
    }

}
