package mb.statix.generator.search;


/**
 * Identity strategy.
 */
public final class IdStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link IdStrategy} class.
     */
    public IdStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // id
        return input;
    }

}
