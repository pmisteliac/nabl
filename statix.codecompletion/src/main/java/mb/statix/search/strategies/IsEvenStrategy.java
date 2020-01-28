package mb.statix.search.strategies;

import mb.statix.search.*;


/**
 * The isEven() strategy.
 */
public final class IsEvenStrategy<CTX> implements Strategy<Integer, Integer, CTX> {

    @Override
    public Sequence<Integer> apply(CTX ctx, Integer input) {
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
