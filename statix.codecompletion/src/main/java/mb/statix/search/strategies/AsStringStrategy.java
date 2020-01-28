package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * The toString strategy.
 */
public final class AsStringStrategy<T> implements SearchStrategy<T, String> {

    @Override
    public Sequence<SearchNode<String>> apply(SearchContext ctx, SearchNode<T> input) {
        return Sequence.of(new SearchNode<>(input.getValue().toString()));
    }

    @Override
    public String toString() {
        return "toString";
    }

}
