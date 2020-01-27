package mb.statix.generator.search;


/**
 * Applies the strategy, but returns the original input when the strategy succeeds.
 */
public final class WhereStrategy implements SStrategy {

    private final SStrategy strategy;

    /**
     * Initializes a new instance of the {@link WhereStrategy} class.
     *
     * @param strategy the strategy
     */
    public WhereStrategy(SStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // where(s) = ?x; s; !x
        StrategyNode result = strategy.apply(context, input);
        if (result.hasFailed()) {
            return StrategyNode.fail();
        } else {
            return input;
        }
    }


    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "where(" + strategy.toString(true) + ")";
    }

}
