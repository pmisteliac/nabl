package mb.statix.search;

/**
 * Listens for search events.
 */
public interface SearchEventListener<T> {

    /**
     * Event that is triggered for nodes that returned both a result and computation and where not cut.
     *
     * @param node the node that was stepped
     */
    void step(SearchNode<T> node);

    /**
     * Event that is triggered for nodes that have been cut.
     *
     * @param node the node that was cut
     */
    void cut(SearchNode<T> node);

    /**
     * Event that is triggered for nodes that have no results.
     *
     * @param node the node that failed
     */
    void fail(SearchNode<T> node);

    /**
     * Event that is triggered for nodes that have no more computations.
     *
     * @param node the node that is done
     */
    void done(SearchNode<T> node);

}
