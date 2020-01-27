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
 * The not(s) strategy, which applies s, then succeeds if s fails and fails if s succeeds.
 */
public final class NotStrategy<T> implements SearchStrategy<T> {

    private final SearchStrategy<T> tryStrategy;

    public NotStrategy(SearchStrategy<T> tryStrategy) {
        this.tryStrategy = tryStrategy;
    }

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        SearchNode<T> branch2 = new SearchNode<>(input.getValue(), next);
        SearchNode<T> branch1 = new SearchNode<>(input.getValue(), new SearchComputation<>(this.tryStrategy,
                new SearchComputation<>(new CutStrategy<>(Collections.singletonList(branch2)), next)));

        return Arrays.asList(
                branch1,
                branch2
        );
    }

    @Override
    public String toString() {
        return "not(" + tryStrategy.toString() + ")";
    }

}
