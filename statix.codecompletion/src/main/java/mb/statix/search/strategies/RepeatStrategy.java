package mb.statix.search.strategies;

import mb.statix.sequences.Sequence;
import mb.statix.search.Strategy;


/**
 * The repeat(s) strategy, which applies s until it fails.
 */
public final class RepeatStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private final Strategy<T, T, CTX> tryStrategy;

    public RepeatStrategy(Strategy<T, T, CTX> tryStrategy) {
        this.tryStrategy = tryStrategy;
    }

    @Override
    public Sequence<T> apply(CTX ctx, T input) throws InterruptedException {
        Sequence<T> elseBranch = Sequence.of(input);
        Sequence<T> thenBranch = new AndStrategy<>(this.tryStrategy, new AndStrategy<>(
                new CutStrategy<>(elseBranch), this)).apply(ctx, input);

        return thenBranch.concatWith(elseBranch);
    }

    @Override
    public String toString() {
        return "repeat(" + tryStrategy.toString() + ")";
    }

}
