package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * The non-deterministic or() strategy, which splits the search tree.
 */
public final class OrStrategy<T> implements SearchStrategy<T> {

    private final SearchStrategy<T> strategy1;
    private final SearchStrategy<T> strategy2;

    public OrStrategy(SearchStrategy<T> strategy1, SearchStrategy<T> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        return Arrays.asList(
                new SearchNode<>(input.getValue(), new SearchComputation<>(this.strategy1, next)),
                new SearchNode<>(input.getValue(), new SearchComputation<>(this.strategy2, next))
        );
    }

    @Override
    public String toString() {
        return strategy1.toString() + " + " + strategy2.toString();
    }

}
