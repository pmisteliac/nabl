package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The fail() strategy, which always fails.
 */
public final class FailStrategy<T, CTX> implements Strategy<T, T, CTX> {

    @Override
    public Sequence<T> apply(CTX ctx, T input) {
        return Sequence.empty();
    }

    @Override
    public String toString() {
        return "fail";
    }

}
