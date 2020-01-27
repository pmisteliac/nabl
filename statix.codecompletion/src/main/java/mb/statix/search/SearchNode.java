package mb.statix.search;


import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * A node in the search graph.
 *
 * This represent the search state and a computation to perform on the state.
 */
public final class SearchNode<T> {

    private boolean cut = false;
    private final T value;
    @Nullable private final SearchComputation<T> computation;

    /**
     * Initializes a new instance of the {@link SearchNode} class.
     *
     * @param value the value
     * @param computation the computation to perform on the state; or {@code null} when done
     */
    public SearchNode(T value, @Nullable SearchComputation<T> computation) {
        this.value = value;
        this.computation = computation;
    }

    /** Gets the value. */
    public T getValue() { return this.value; }

    /**
     * Cuts this node.
     */
    public void cut() {
        this.cut = true;
    }

    /**
     * Evaluates the search computation.
     *
     * @param ctx the search context
     * @return the new nodes
     */
    public List<SearchNode<T>> step(SearchContext ctx, SearchEventListener<T> eventListener) {
        if (this.cut) {
            // cut
            eventListener.cut(this);
            return Collections.emptyList();
        } else if (this.computation == null) {
            // done
            eventListener.done(this);
            return Collections.emptyList();
        } else {
            List<SearchNode<T>> nodes = this.computation.getStrategy().eval(ctx, this, this.computation.getNext());
            if (nodes.isEmpty()) {
                // fail
                eventListener.fail(this);
                return Collections.emptyList();
            } else {
                // continue
                eventListener.step(this);
                return nodes;
            }
        }
    }
}
