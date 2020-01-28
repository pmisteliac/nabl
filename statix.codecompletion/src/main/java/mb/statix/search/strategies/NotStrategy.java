package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The not(s) strategy, which applies s, then succeeds if s fails and fails if s succeeds.
 */
public final class NotStrategy<T> implements SearchStrategy<T, T> {

    private final SearchStrategy<T, T> tryStrategy;

    public NotStrategy(SearchStrategy<T, T> tryStrategy) {
        this.tryStrategy = tryStrategy;
    }

    @Override
    public Sequence<SearchNode<T>> apply(SearchContext ctx, SearchNode<T> input) {
        Sequence<SearchNode<T>> elseBranch = Sequence.of(input);
        Sequence<SearchNode<T>> thenBranch = new SeqStrategy<>(
                this.tryStrategy, new SeqStrategy<>(
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
