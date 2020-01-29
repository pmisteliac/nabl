package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The inc() strategy.
 */
public final class IncStrategy<CTX> implements Strategy<Integer, Integer, CTX> {

    @Override
    public Sequence<Integer> apply(CTX ctx, Integer input) throws InterruptedException {
        return Sequence.of(input + 1);
    }

    @Override
    public String toString() {
        return "inc";
    }

}
