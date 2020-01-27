package mb.statix.search.strategies;

import mb.statix.search.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * The (s < t + e) guarded choice strategy, which applies s, then t if it succeeds or e if it fails.
 */
public final class GChoiceStrategy<T> implements SearchStrategy<T> {

    private final SearchStrategy<T> tryStrategy;
    private final SearchStrategy<T> thenStrategy;
    private final SearchStrategy<T> elseStrategy;

    public GChoiceStrategy(SearchStrategy<T> tryStrategy, SearchStrategy<T> thenStrategy, SearchStrategy<T> elseStrategy) {
        this.tryStrategy = tryStrategy;
        this.thenStrategy = thenStrategy;
        this.elseStrategy = elseStrategy;
    }

    @Override
    public List<SearchNode<T>> eval(SearchContext ctx, SearchNode<T> input, @Nullable SearchComputation<T> next) {
        SearchNode<T> branch2 = new SearchNode<>(input.getValue(), new SearchComputation<>(this.tryStrategy,
                new SearchComputation<>(this.elseStrategy, next)));
        SearchNode<T> branch1 = new SearchNode<>(input.getValue(), new SearchComputation<>(this.tryStrategy,
                new SearchComputation<>(new CutStrategy<>(Collections.singletonList(branch2)),
                        new SearchComputation<>(this.thenStrategy, next))));

        return Arrays.asList(
                branch1,
                branch2
        );
    }

    @Override
    public String toString() {
        return tryStrategy.toString() + " < " + thenStrategy.toString() + " + " + elseStrategy.toString();
    }

}
