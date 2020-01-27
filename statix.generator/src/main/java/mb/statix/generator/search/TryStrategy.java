package mb.statix.generator.search;


/**
 * Applies a strategy if it succeeds.
 */
public final class TryStrategy implements SStrategy {

    private final SStrategy strategy;

    /**
     * Initializes a new instance of the {@link TryStrategy} class.
     *
     * @param strategy the strategy to try
     */
    public TryStrategy(SStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // try(s) = s <+ id
        return new LChoiceStrategy(this.strategy, new IdStrategy()).apply(context, input);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "try(" + strategy.toString(true) + ")";
    }

}
