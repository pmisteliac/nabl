package mb.statix.generator.search;


/**
 * Applies a strategy, then another strategy; or applied an alternative strategy if the first fails.
 */
public final class GChoiceStrategy implements SStrategy {

    private final SStrategy strategy1;
    private final SStrategy strategy2;
    private final SStrategy strategy3;

    /**
     * Initializes a new instance of the {@link GChoiceStrategy} class.
     *
     * @param strategy1 the first strategy
     * @param strategy2 the second strategy
     * @param strategy3 the third strategy
     */
    public GChoiceStrategy(SStrategy strategy1, SStrategy strategy2, SStrategy strategy3) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
        this.strategy3 = strategy3;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        StrategyNode result1 = strategy1.apply(context, input);
        if (!result1.hasFailed()) {
            return strategy2.apply(context, result1);
        } else {
            return strategy3.apply(context, input);
        }
    }

}
