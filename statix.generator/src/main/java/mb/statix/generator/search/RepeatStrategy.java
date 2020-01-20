package mb.statix.generator.search;


/**
 * Repeatedly applies a strategy until it fails.
 */
public final class RepeatStrategy implements SStrategy {

    private final SStrategy strategy;

    /**
     * Initializes a new instance of the {@link RepeatStrategy} class.
     *
     * @param strategy the strategy to repeat
     */
    public RepeatStrategy(SStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // try(s; repeat(s))
        StrategyNode result = strategy.apply(context, input);;
        while (!result.hasFailed()) {
            result = strategy.apply(context, result);
        }
        return result;
    }

}
