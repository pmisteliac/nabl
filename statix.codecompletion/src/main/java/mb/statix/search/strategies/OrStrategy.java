package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The non-deterministic or() strategy, which splits the search tree.
 */
public final class OrStrategy<I, O> implements SearchStrategy<I, O> {

    private final SearchStrategy<I, O> strategy1;
    private final SearchStrategy<I, O> strategy2;

    public OrStrategy(SearchStrategy<I, O> strategy1, SearchStrategy<I, O> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Sequence<SearchNode<O>> apply(SearchContext ctx, SearchNode<I> input) {
        return this.strategy1.apply(ctx, input).concatWith(this.strategy2.apply(ctx, input));
    }

    @Override
    public String toString() {
        return strategy1.toString() + " + " + strategy2.toString();
    }

}
