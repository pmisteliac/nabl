package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * The fail() strategy, which always fails.
 */
public final class FailStrategy<T> implements SearchStrategy<T> {

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "fail";
    }

}
