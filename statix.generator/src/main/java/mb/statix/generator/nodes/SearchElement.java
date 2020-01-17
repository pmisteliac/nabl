package mb.statix.generator.nodes;

import javax.annotation.Nullable;


/**
 * An element in the graph of search nodes.
 */
public interface SearchElement {

    /**
     * Gets a description of the node.
     *
     * @return a description
     */
    String desc();

    /**
     * Gets the parent of the node.
     *
     * @return a parent node; or {@code null} when this is the root node
     */
    @Nullable
    SearchNode<?> parent();

}