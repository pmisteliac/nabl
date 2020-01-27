package mb.statix.generator.search;


/**
 * Fail strategy.
 */
public final class FailStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link FailStrategy} class.
     */
    public FailStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // fail
        return StrategyNode.fail();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "fail";
    }
}
