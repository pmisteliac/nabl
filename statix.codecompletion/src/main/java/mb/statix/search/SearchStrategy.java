package mb.statix.search;

import javax.annotation.Nullable;
import java.util.List;


/**
 * A search strategy, which takes a search node and produces a list of search nodes.
 * The produced search nodes are fed to the next search strategy in left-to-right order,
 * resulting in a depth-first search.
 */
@FunctionalInterface
public interface SearchStrategy<T> {

    /**
     * Applies the search strategy.
     *
     * @param ctx the search context
     * @param input the input node
     * @param next the next computation; or {@code null} when there is none
     * @return the list of resulting nodes; or an empty list when the strategy failed
     */
    List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next);

}
