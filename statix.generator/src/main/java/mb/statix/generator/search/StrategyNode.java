package mb.statix.generator.search;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * A result of a search strategy.
 */
public final class StrategyNode {

    /**
     * Creates a new instance of the {@link StrategyNode} class.
     *
     * @param states the states in the node
     * @return the created node
     */
    public static StrategyNode of(Stream<StrategySearchState> states) {
        return new StrategyNode(states);
    }

    /**
     * Creates a new instance of the {@link StrategyNode} class with no states.
     *
     * @return the created node
     */
    public static StrategyNode fail() { return new StrategyNode(Stream.of()); }

    private final Stream<StrategySearchState> states;
    private final boolean hasFailed;

    /**
     * Initializes a new instance of the {@link StrategyNode} class.
     *
     * @param states the states in the node
     */
    private StrategyNode(Stream<StrategySearchState> states) {
        // We test whether the stream is empty by trying to iterate the first element.
        // However, this would require us to perform a terminal operation.
        // To do this more efficiently, we recreate a new Spliterator of the stream.
        // While this prevent us from having to buffer all the elements, it also
        // means the computation has become sequential, even if a later operation
        // attempts to make it parallel.
        Iterator<StrategySearchState> iterator = states.iterator();
        this.hasFailed = !iterator.hasNext();
        this.states = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    /**
     * Gets whether this result indicates failure.
     *
     * @return {@code true} when the strategy failed; otherwise, {@code false}
     */
    public boolean hasFailed() { return this.hasFailed; }

    /**
     * Gets the states in this node.
     *
     * @return the states in this node; or empty when the strategy failed
     */
    public Stream<StrategySearchState> getStates() {
        return this.states;
    }

}
