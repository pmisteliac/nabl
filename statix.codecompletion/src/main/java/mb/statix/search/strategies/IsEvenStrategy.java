package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;


/**
 * The isEven() strategy.
 */
public final class IsEvenStrategy<CTX> implements Strategy<Integer, Integer, CTX> {

    @Override
    public Sequence<Integer> apply(CTX ctx, Integer input) throws InterruptedException {
        if (input % 2 == 0) {
            return Sequence.of(input);
        } else {
            return Sequence.empty();
        }
    }

    @Override
    public String toString() {
        return "isEven";
    }

}
