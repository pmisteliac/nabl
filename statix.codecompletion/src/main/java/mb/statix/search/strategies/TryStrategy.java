package mb.statix.search.strategies;

import mb.statix.search.Sequence;
import mb.statix.search.Strategy;


/**
 * The try(s) strategy, which applies s but always succeeds.
 */
public final class TryStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private final Strategy<T, T, CTX> tryStrategy;

    public TryStrategy(Strategy<T, T, CTX> tryStrategy) {
        this.tryStrategy = tryStrategy;
    }

    @Override
    public Sequence<T> apply(CTX ctx, T input) throws InterruptedException {
        Sequence<T> elseBranch = Sequence.of(input);
        Sequence<T> thenBranch = new AndStrategy<>(this.tryStrategy, new CutStrategy<>(elseBranch)).apply(ctx, input);

        return thenBranch.concatWith(elseBranch);
    }

    @Override
    public String toString() {
        return "try(" + tryStrategy.toString() + ")";
    }

}
