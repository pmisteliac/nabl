package mb.statix.search.strategies.teststrategies;

import mb.statix.search.Strategy;

import java.util.Collection;
import java.util.stream.Stream;


/**
 * Test strategy that ignores the input and provides the given list of values.
 *
 * @param <CTX> the type of context
 */
public final class DataStrategy<I, O, CTX> implements Strategy<I, O, CTX> {

    private final Collection<O> data;

    public DataStrategy(Collection<O> data) {
        this.data = data;
    }

    @Override
    public Stream<O> apply(CTX ctx, I input) throws InterruptedException {
        return data.stream();
    }

}
