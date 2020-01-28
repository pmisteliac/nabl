package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The inc() strategy.
 */
public final class IncStrategy implements SearchStrategy<Integer, Integer> {

    @Override
    public Sequence<SearchNode<Integer>> apply(SearchContext ctx, SearchNode<Integer> input) {
        return Sequence.of(new SearchNode<>(input.getValue() + 1));
    }

    @Override
    public String toString() {
        return "inc";
    }

}
