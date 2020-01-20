package mb.statix.generator.search;


/**
 * Succeeds if the strategy fails; otherwise, fails.
 */
public final class NotStrategy implements SStrategy {

    private final SStrategy strategy;

    /**
     * Initializes a new instance of the {@link NotStrategy} class.
     *
     * @param strategy the strategy
     */
    public NotStrategy(SStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // not(s) = s < fail + id
        return new GChoiceStrategy(this.strategy, new FailStrategy(), new IdStrategy()).apply(context, input);
    }

}
