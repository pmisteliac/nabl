package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The isEven() strategy.
 */
public final class IsEvenStrategy implements SearchStrategy<Integer, Integer> {

    @Override
    public Sequence<SearchNode<Integer>> apply(SearchContext ctx, SearchNode<Integer> input) {
        if (input.getValue() % 2 == 0) {
            return Sequence.of(new SearchNode<>(input.getValue()));
        } else {
            return Sequence.empty();
        }
    }

    @Override
    public String toString() {
        return "isEven";
    }

}
