package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The s1; s2 strategy.
 */
public final class SeqStrategy<T> implements SearchStrategy<T> {

    private final SearchStrategy<T> strategy1;
    private final SearchStrategy<T> strategy2;

    public SeqStrategy(SearchStrategy<T> strategy1, SearchStrategy<T> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        return Collections.singletonList(new SearchNode<>(input.getValue(), new SearchComputation<>(this.strategy1, new SearchComputation<T>(this.strategy2, next))));
    }

    @Override
    public String toString() {
        return "(" + strategy1.toString() + "; " + strategy2.toString() + ")";
    }

}
