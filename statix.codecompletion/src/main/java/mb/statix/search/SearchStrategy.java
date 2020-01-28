package mb.statix.search;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


/**
 * A search strategy, which takes a search node and produces a list of search nodes.
 * The produced search nodes are fed to the next search strategy in left-to-right order,
 * resulting in a depth-first search.
 */
@FunctionalInterface
public interface SearchStrategy<I, O> {

    /**
     * Applies the search strategy.
     *
     * @param ctx the search context
     * @param input the input node
     * @return a sequence of resulting nodes; or an empty sequence when the strategy failed
     */
    Sequence<SearchNode<O>> apply(SearchContext ctx, SearchNode<I> input);

}
