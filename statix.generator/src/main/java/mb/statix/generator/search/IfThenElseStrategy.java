package mb.statix.generator.search;


/**
 * If-then-else strategy.
 */
public final class IfThenElseStrategy implements SStrategy {

    private final SStrategy strategyC;
    private final SStrategy strategyT;
    private final SStrategy strategyE;

    /**
     * Initializes a new instance of the {@link IfThenElseStrategy} class.
     *
     * @param strategyC the first strategy
     * @param strategyT the second strategy
     * @param strategyE the third strategy
     */
    public IfThenElseStrategy(SStrategy strategyC, SStrategy strategyT, SStrategy strategyE) {
        this.strategyC = strategyC;
        this.strategyT = strategyT;
        this.strategyE = strategyE;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        // if s1 then s2 else s3 end = where(s1) < s2 + s3
        return new GChoiceStrategy(new WhereStrategy(this.strategyC), this.strategyT, this.strategyE).apply(context, input);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        if (inParens)
            return "if " + strategyC.toString(false) + " then " + strategyT.toString(false) + " else " + strategyE.toString(false);
        else
            return "(if " + strategyC.toString(false) + " then " + strategyT.toString(false) + " else " + strategyE.toString(false) + ")";
    }

}
