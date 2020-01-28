package mb.statix.search.strategies;

import mb.statix.search.SearchStrategy;
import mb.statix.search.Sequence;


/**
 * Convenience functions for creating strategies.
 */
public final class Strategies {

    public static <T> CutStrategy<T> cut(Sequence<?> sequence) {
        return new CutStrategy<>(sequence);
    }

    public static <T> FailStrategy<T> fail() {
        return new FailStrategy<>();
    }

    public static <A, B, C> GChoiceStrategy<A, B, C> gChoice(SearchStrategy<A, B> tryStrategy, SearchStrategy<B, C> thenStrategy, SearchStrategy<A, C> elseStrategy) {
        return new GChoiceStrategy<>(tryStrategy, thenStrategy, elseStrategy);
    }

    public static <T> IdStrategy<T> id() {
        return new IdStrategy<>();
    }

    public static IncStrategy inc() {
        return new IncStrategy();
    }

    public static IsEvenStrategy isEven() {
        return new IsEvenStrategy();
    }

    public static <T> NotStrategy<T> not(SearchStrategy<T, T> strategy) {
        return new NotStrategy<T>(strategy);
    }

    public static <T, R> OrStrategy<T, R> or(SearchStrategy<T, R> strategy1, SearchStrategy<T, R> strategy2) {
        return new OrStrategy<>(strategy1, strategy2);
    }

    public static <A, B, C> SeqStrategy<A, B, C> seq(SearchStrategy<A, B> strategy1, SearchStrategy<B, C> strategy2) {
        return new SeqStrategy<>(strategy1, strategy2);
    }

    public static <T> AsStringStrategy<T> asString() {
        return new AsStringStrategy<>();
    }


}
