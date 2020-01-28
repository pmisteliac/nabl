package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * The fail() strategy, which always fails.
 */
public final class FailStrategy<T> implements SearchStrategy<T, T> {

    @Override
    public Sequence<SearchNode<T>> apply(SearchContext ctx, SearchNode<T> input) {
        return Sequence.empty();
    }

    @Override
    public String toString() {
        return "fail";
    }

}
