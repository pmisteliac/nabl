package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The inc() strategy.
 */
public final class IncStrategy implements SearchStrategy<Integer> {

    @Override
    public List<SearchNode<Integer>> eval(SearchContext ctx, SearchNode<Integer> input, @Nullable SearchComputation<Integer> next) {
        return Collections.singletonList(new SearchNode<>(input.getValue() + 1, next));
    }

    @Override
    public String toString() {
        return "inc";
    }

}
