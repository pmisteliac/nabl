package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


/**
 * The ! strategy, which always succeeds and cuts the branches.
 */
public final class CutStrategy<T> implements SearchStrategy<T, T> {

    private Sequence<?> branches;

    /**
     * Initializes a new instance of the {@link CutStrategy} class.
     * @param branches the branches to cut when this is evaluated
     */
    public CutStrategy(Sequence<?> branches) {
        this.branches = branches;
    }

    @Override
    public Sequence<SearchNode<T>> apply(SearchContext ctx, SearchNode<T> input) {
        this.branches.cut();
        return Sequence.of(new SearchNode<>(input.getValue()));
    }

    @Override
    public String toString() {
        return "!";
    }

}
