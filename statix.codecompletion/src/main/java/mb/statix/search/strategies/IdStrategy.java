package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The id() strategy, which always succeeds.
 */
public final class IdStrategy<T> implements SearchStrategy<T, T> {

    @Override
    public Sequence<SearchNode<T>> apply(SearchContext ctx, SearchNode<T> input) {
        return Sequence.of(new SearchNode<>(input.getValue()));
    }

    @Override
    public String toString() {
        return "id";
    }

}
