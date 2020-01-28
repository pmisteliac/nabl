package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The (s < t + e) guarded choice strategy, which applies s, then t if it succeeds or e if it fails.
 */
public final class GChoiceStrategy<A, B, C, CTX> implements Strategy<A, C, CTX> {

    private final Strategy<A, B, CTX> tryStrategy;
    private final Strategy<B, C, CTX> thenStrategy;
    private final Strategy<A, C, CTX> elseStrategy;

    public GChoiceStrategy(Strategy<A, B, CTX> tryStrategy, Strategy<B, C, CTX> thenStrategy, Strategy<A, C, CTX> elseStrategy) {
        this.tryStrategy = tryStrategy;
        this.thenStrategy = thenStrategy;
        this.elseStrategy = elseStrategy;
    }

    @Override
    public Sequence<C> apply(CTX ctx, A input) {
        Sequence<C> elseBranch = this.elseStrategy.apply(ctx, input);
        Sequence<C> thenBranch = new SeqStrategy<>(
                this.tryStrategy, new SeqStrategy<>(
                        new CutStrategy<>(elseBranch),
                        this.thenStrategy))
                .apply(ctx, input);

        return thenBranch.concatWith(elseBranch);
    }

    @Override
    public String toString() {
        return tryStrategy.toString() + " < " + thenStrategy.toString() + " + " + elseStrategy.toString();
    }

}
