package mb.statix.search.strategies;

import mb.statix.search.Strategy;
import mb.statix.search.Sequence;


/**
 * Convenience functions for creating strategies.
 */
public final class Strategies {

    public static <T, CTX> CutStrategy<T, CTX> cut(Sequence<?> sequence) {
        return new CutStrategy<>(sequence);
    }

    public static <T, CTX> FailStrategy<T, CTX> fail() {
        return new FailStrategy<>();
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

    public static <CTX> IsEvenStrategy<CTX> isEven() {
        return new IsEvenStrategy<>();
    }

    public static <T, CTX> NotStrategy<T, CTX> not(Strategy<T, T, CTX> strategy) {
        return new NotStrategy<>(strategy);
    }

    public static <T, R, CTX> OrStrategy<T, R, CTX> or(Strategy<T, R, CTX> strategy1, Strategy<T, R, CTX> strategy2) {
        return new OrStrategy<>(strategy1, strategy2);
    }

    public static <A, B, C, CTX> SeqStrategy<A, B, C, CTX> seq(Strategy<A, B, CTX> strategy1, Strategy<B, C, CTX> strategy2) {
        return new SeqStrategy<>(strategy1, strategy2);
    }

    public static <T, CTX> AsStringStrategy<T, CTX> asString() {
        return new AsStringStrategy<>();
    }


}
