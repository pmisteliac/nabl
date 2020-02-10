package mb.statix.search.strategies.teststrategies;

import mb.statix.search.Strategy;

import java.util.stream.Stream;


/**
 * Test strategy that increments an integer,
 * and counts how many times it has been invoked.
 *
 * @param <CTX> the type of context
 */
public final class IncrementStrategy<CTX> implements Strategy<Integer, Integer, CTX> {

    @Override
    public Stream<Integer> apply(CTX ctx, Integer input) throws InterruptedException {
        return Stream.of(input + 1);
    }

}
