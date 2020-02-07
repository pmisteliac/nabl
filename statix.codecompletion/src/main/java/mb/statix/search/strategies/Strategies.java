package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;
import mb.statix.solver.IConstraint;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Convenience functions for creating strategies.
 */
public final class Strategies {

    public static <A, B, C, CTX> AndStrategy<A, B, C, CTX> and(Strategy<A, B, CTX> strategy1, Strategy<B, C, CTX> strategy2) {
        return new AndStrategy<>(strategy1, strategy2);
    }

    public static <T, CTX> AsStringStrategy<T, CTX> asString() {
        return new AsStringStrategy<>();
    }

    public static <T, CTX> CutStrategy<T, CTX> cut(Sequence<?> sequence) {
        return new CutStrategy<>(sequence);
    }

    public static <T, R, CTX> DebugStrategy<T, R, CTX> debug(Strategy<T, R, CTX> strategy, Consumer<R> action) {
        return new DebugStrategy<>(strategy, action);
    }

    public static DelayStuckQueriesStrategy delayStuckQueries() {
        return new DelayStuckQueriesStrategy();
    }

    public static ExpandQueryStrategy expandQuery() {
        return new ExpandQueryStrategy();
    }

    public static ExpandRuleStrategy expandRule() {
        return new ExpandRuleStrategy();
    }

    public static <T, CTX> FailStrategy<T, CTX> fail() {
        return new FailStrategy<>();
    }

    public static <C extends IConstraint> FocusStrategy<C> focus(Class<C> constraintClass, Predicate<C> predicate) {
        return new FocusStrategy<>(constraintClass, predicate);
    }

    public static <C extends IConstraint> FocusStrategy<C> focus(Class<C> constraintClass) {
        return focus(constraintClass, c -> true);
    }

    public static <A, B, C, CTX> GChoiceStrategy<A, B, C, CTX> gChoice(Strategy<A, B, CTX> tryStrategy, Strategy<B, C, CTX> thenStrategy, Strategy<A, C, CTX> elseStrategy) {
        return new GChoiceStrategy<>(tryStrategy, thenStrategy, elseStrategy);
    }

    public static <T, CTX> IdStrategy<T, CTX> id() {
        return new IdStrategy<>();
    }

    public static <CTX> IncStrategy<CTX> inc() {
        return new IncStrategy();
    }

    public static InferStrategy infer() {
        return new InferStrategy();
    }

    public static <CTX> IsEvenStrategy<CTX> isEven() {
        return new IsEvenStrategy<>();
    }

    public static <A, B, CTX> LimitStrategy<A, B, CTX> limit(int limit, Strategy<A, B, CTX> strategy) {
        return new LimitStrategy<>(limit, strategy);
    }

    public static <T, CTX> NotStrategy<T, CTX> not(Strategy<T, T, CTX> strategy) {
        return new NotStrategy<>(strategy);
    }

    public static <T, R, CTX> OrStrategy<T, R, CTX> or(Strategy<T, R, CTX> strategy1, Strategy<T, R, CTX> strategy2) {
        return new OrStrategy<>(strategy1, strategy2);
    }

    public static <I, O, CTX> DebugStrategy<I, O, CTX> print(Strategy<I, O, CTX> strategy) {
        return new DebugStrategy<>(strategy, s -> System.out.println(s.toString()));
    }

    public static <T, CTX> RepeatStrategy<T, CTX> repeat(Strategy<T, T, CTX> strategy) {
        return new RepeatStrategy<>(strategy);
    }

    public static <I, O, CTX> SeqStrategy.Builder<I, O, CTX> seq(Strategy<I, O, CTX> strategy) {
        return new SeqStrategy.Builder<>(strategy);
    }

    public static <A, B, CTX> ShuffleStrategy<A, B, CTX> shuffle(Random rng, Strategy<A, B, CTX> strategy) {
        return new ShuffleStrategy<>(rng, strategy);
    }

    public static <A, B, CTX> ShuffleStrategy<A, B, CTX> shuffle(Strategy<A, B, CTX> strategy) {
        return shuffle(new Random(), strategy);
    }

    public static <T, CTX> TryStrategy<T, CTX> try_(Strategy<T, T, CTX> strategy) {
        return new TryStrategy<>(strategy);
    }

}
