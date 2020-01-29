package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The toString strategy.
 */
public final class AsStringStrategy<T, CTX> implements Strategy<T, String, CTX> {

    @Override
    public Sequence<String> apply(CTX ctx, T input) throws InterruptedException {
        return Sequence.of(input.toString());
    }

    @Override
    public String toString() {
        return "toString";
    }

}
