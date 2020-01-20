package mb.statix.generator.search;

import java.util.stream.Stream;


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

    /**
     * Initializes a new instance of the {@link StrategyNode} class.
     *
     * @param states the states in the node
     */
    private StrategyNode(Stream<StrategySearchState> states) {
        this.states = states;
    }

//    /**
//     * Gets whether this result indicates failure.
//     *
//     * @return {@code true} when the strategy failed; otherwise, {@code false}
//     */
//    public boolean hasFailed();

    /**
     * Gets the states in this node.
     *
     * @return the states in this node; or empty when the strategy failed
     */
    public Stream<StrategySearchState> getStates() {
        return this.states;
    }

}
