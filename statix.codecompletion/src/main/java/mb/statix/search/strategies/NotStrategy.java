package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;


/**
 * The not(s) strategy, which applies s, then succeeds if s fails and fails if s succeeds.
 */
public final class NotStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private final Strategy<T, T, CTX> tryStrategy;

    public NotStrategy(Strategy<T, T, CTX> tryStrategy) {
        this.tryStrategy = tryStrategy;
    }

    @Override
    public Sequence<T> apply(CTX ctx, T input) throws InterruptedException {
        Sequence<T> elseBranch = Sequence.of(input);
        Sequence<T> thenBranch = new AndStrategy<>(
                this.tryStrategy, new AndStrategy<>(
                new CutStrategy<>(elseBranch),
                new FailStrategy<>()))
                .apply(ctx, input);

        return thenBranch.concatWith(elseBranch);
    }

    @Override
    public String toString() {
        return "not(" + tryStrategy.toString() + ")";
    }

}
