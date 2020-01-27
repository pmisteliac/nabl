package mb.statix.search;

import javax.annotation.Nullable;
import java.util.List;


/**
 * A search computation.
 */
public final class SearchComputation<T> {

    private final SearchStrategy<T> strategy;
    @Nullable private final SearchComputation<T> next;

    /**
     * Initializes a new instance of the {@link SearchComputation} class.
     *
     * @param strategy the search strategy to evaluate
     * @param next the next computation to perform
     */
    public SearchComputation(SearchStrategy<T> strategy, @Nullable SearchComputation<T> next) {
        this.strategy = strategy;
        this.next = next;
    }

    public SearchStrategy<T> getStrategy() {
        return this.strategy;
    }

    @Nullable
    public SearchComputation<T> getNext() {
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
