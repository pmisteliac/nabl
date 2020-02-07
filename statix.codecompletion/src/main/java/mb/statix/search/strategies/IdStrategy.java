package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;


/**
 * The id() strategy, which always succeeds.
 */
public final class IdStrategy<T, CTX> implements Strategy<T, T, CTX> {

    @Override
    public Sequence<T> apply(CTX ctx, T input) throws InterruptedException {
        return Sequence.of(input);
    }

    @Override
    public String toString() {
        return "id";
    }

}
