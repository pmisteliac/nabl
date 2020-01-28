package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The s1; s2 strategy.
 */
public final class SeqStrategy<A, B, C> implements SearchStrategy<A, C> {

    private final SearchStrategy<A, B> strategy1;
    private final SearchStrategy<B, C> strategy2;

    public SeqStrategy(SearchStrategy<A, B> strategy1, SearchStrategy<B, C> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Sequence<SearchNode<C>> apply(SearchContext ctx, SearchNode<A> input){
        return this.strategy1.apply(ctx, input).flatMap(n -> this.strategy2.apply(ctx, n));
    }

    @Override
    public String toString() {
        return "(" + strategy1.toString() + "; " + strategy2.toString() + ")";
    }

}
