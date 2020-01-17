package mb.statix.generator.nodes;

import java.util.Objects;

import mb.statix.generator.SearchState;

import javax.annotation.Nullable;

/**
 * A search node in the search graph.
 */
public final class SearchNode<O extends SearchState> implements SearchElement {

    private final int id;
    private final O output;
    private final SearchNode<?> parent;
    private final String desc;

    /**
     * Initializes a new instance of the {@link SearchNode} class.
     *
     * @param id a unique node ID identifying this node in the search graph
     * @param output the search state in this node
     * @param parent the parent of the search node; or {@code null} when this is the root node
     * @param desc a description of the node
     */
    public SearchNode(int id, O output, @Nullable SearchNode<?> parent, String desc) {
        this.id = id;
        this.output = output;
        this.parent = parent;
        this.desc = desc;
    }

    /**
     * Gets a unique node ID identifying this node in the search graph.
     *
     * @return a unique node ID
     */
    public int id() {
        return id;
    }

    /**
     * Gets the search state in this node.
     *
     * @return a search state
     */
    public O output() {
        return output;
    }

    public <X extends SearchState> SearchNode<X> withOutput(X output) {
        return new SearchNode<>(id, output, parent, desc);
    }

    @Override @Nullable public SearchNode<?> parent() {
        return parent;
    }

    @Override public String desc() {
        return desc;
    }

    @Override public String toString() {
        return desc != null ? desc : Objects.toString(this);
    }

}