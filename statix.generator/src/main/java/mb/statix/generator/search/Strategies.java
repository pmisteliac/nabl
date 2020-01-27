package mb.statix.generator.search;

import mb.statix.solver.IConstraint;
import org.metaborg.util.log.LoggerUtils;

import java.util.Arrays;
import java.util.function.Predicate;


public final class Strategies {

    public static DebugStrategy debug(String name) {
        return new DebugStrategy(LoggerUtils.logger(DebugStrategy.class), name);
    }

    public static DelayStuckQueriesStrategy delayStuckQueries() {
        return new DelayStuckQueriesStrategy();
    }

    public static ExpandQueryStrategy expandQuery() {
        return new ExpandQueryStrategy(null /* TODO */);
    }

    public static ExpandRuleStrategy expandRule() {
        return new ExpandRuleStrategy();
    }

    public static FailStrategy fail() {
        return new FailStrategy();
    }

    public static FocusStrategy focus(Predicate<IConstraint> selector) {
        return new FocusStrategy(selector);
    }

    public static GChoiceStrategy gChoice(SStrategy strategy1, SStrategy strategy2, SStrategy strategy3) {
        return new GChoiceStrategy(strategy1, strategy2, strategy3);
    }

    public static IdStrategy id() {
        return new IdStrategy();
    }

    public static IfThenElseStrategy ifThenElse(SStrategy strategyC, SStrategy strategyT, SStrategy strategyE) {
        return new IfThenElseStrategy(strategyC, strategyT, strategyE);
    }

    public static InferStrategy infer() {
        return new InferStrategy();
    }

    public static LChoiceStrategy either(SStrategy strategy1, SStrategy strategy2) {
        return new LChoiceStrategy(strategy1, strategy2);
    }

    public static LimitStrategy limit(int limit) {
        return new LimitStrategy(limit);
    }

    public static NotStrategy not(SStrategy strategy) {
        return new NotStrategy(strategy);
    }

    public static RepeatStrategy repeat(SStrategy strategy) {
        return new RepeatStrategy(strategy);
    }

    public static SeqStrategy seq(SStrategy... strategies) {
        return new SeqStrategy(Arrays.asList(strategies));
    }

    public static SingleStrategy single() {
        return new SingleStrategy();
    }

    public static TryStrategy try_(SStrategy strategy) {
        return new TryStrategy(strategy);
    }

    public static UnfocusStrategy unfocus() {
        return new UnfocusStrategy();
    }

    public static WhereStrategy where(SStrategy strategy) {
        return new WhereStrategy(strategy);
    }

}
