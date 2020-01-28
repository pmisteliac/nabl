package mb.statix.search;

import javax.annotation.Nullable;
import java.util.List;


/**
 * A search computation.
 */
public final class SearchComputation<I, O> {

    private final SearchStrategy<I, O> strategy;
    @Nullable private final SearchComputation<O, O> next;

    /**
     * Initializes a new instance of the {@link SearchComputation} class.
     *
     * @param strategy the search strategy to evaluate
     * @param next the next computation to perform
     */
    public SearchComputation(SearchStrategy<I, O> strategy, @Nullable SearchComputation<O, O> next) {
        this.strategy = strategy;
        this.next = next;
    }

    public SearchStrategy<I, O> getStrategy() {
        return this.strategy;
    }

    @Nullable
    public SearchComputation<O, O> getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        if (this.next != null) {
            return this.strategy.toString() + "; " + this.next.toString();
        } else {
            return this.strategy.toString();
        }
    }

}
