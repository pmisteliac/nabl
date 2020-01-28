package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The (s < t + e) guarded choice strategy, which applies s, then t if it succeeds or e if it fails.
 */
public final class GChoiceStrategy<A, B, C> implements SearchStrategy<A, C> {

    private final SearchStrategy<A, B> tryStrategy;
    private final SearchStrategy<B, C> thenStrategy;
    private final SearchStrategy<A, C> elseStrategy;

    public GChoiceStrategy(SearchStrategy<A, B> tryStrategy, SearchStrategy<B, C> thenStrategy, SearchStrategy<A, C> elseStrategy) {
        this.tryStrategy = tryStrategy;
        this.thenStrategy = thenStrategy;
        this.elseStrategy = elseStrategy;
    }

    @Override
    public Sequence<SearchNode<C>> apply(SearchContext ctx, SearchNode<A> input) {
        Sequence<SearchNode<C>> elseBranch = this.elseStrategy.apply(ctx, input);
        Sequence<SearchNode<C>> thenBranch = new SeqStrategy<>(
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
