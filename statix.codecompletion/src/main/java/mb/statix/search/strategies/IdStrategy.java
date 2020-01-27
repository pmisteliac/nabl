package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The id() strategy, which always succeeds.
 */
public final class IdStrategy<T> implements SearchStrategy<T> {

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        return Collections.singletonList(new SearchNode<>(input.getValue(), next));
    }

    @Override
    public String toString() {
        return "id";
    }

}
