package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The ! strategy, which always succeeds and cuts the branches.
 */
public final class CutStrategy<T> implements SearchStrategy<T> {

    private List<SearchNode<?>> branches;

    /**
     * Initializes a new instance of the {@link CutStrategy} class.
     * @param branches the branches to cut when this is evaluated
     */
    public CutStrategy(List<SearchNode<?>> branches) {
        this.branches = branches;
    }

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        this.branches.forEach(SearchNode::cut);
        return Collections.singletonList(new SearchNode<>(input.getValue(), next));
    }

    @Override
    public String toString() {
        return "!";
    }

}
