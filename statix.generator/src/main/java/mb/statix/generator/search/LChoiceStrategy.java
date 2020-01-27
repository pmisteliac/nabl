package mb.statix.generator.search;


/**
 * Applies a strategy, or another strategy if the first fails.
 */
public final class LChoiceStrategy implements SStrategy {

    private final SStrategy strategy1;
    private final SStrategy strategy2;

    /**
     * Initializes a new instance of the {@link LChoiceStrategy} class.
     *
     * @param strategy1 the first strategy
     * @param strategy2 the second strategy
     */
    public LChoiceStrategy(SStrategy strategy1, SStrategy strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // s1 < id + s2
        return new GChoiceStrategy(this.strategy1, new IdStrategy(), this.strategy2).apply(context, input);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        if (inParens)
            return strategy1.toString(false) + " <+ " + strategy2.toString(false);
        else
            return "(" + strategy1.toString(false) + " <+ " + strategy2.toString(false) + ")";
    }
}
