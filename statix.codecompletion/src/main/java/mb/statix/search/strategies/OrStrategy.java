package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;


/**
 * The non-deterministic or() strategy, which splits the search tree.
 */
public final class OrStrategy<I, O, CTX> implements Strategy<I, O, CTX> {

    private final Strategy<I, O, CTX> strategy1;
    private final Strategy<I, O, CTX> strategy2;

    public OrStrategy(Strategy<I, O, CTX> strategy1, Strategy<I, O, CTX> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Sequence<O> apply(CTX ctx, I input) throws InterruptedException {
        return this.strategy1.apply(ctx, input).concatWith(this.strategy2.apply(ctx, input));
    }

    @Override
    public String toString() {
        return strategy1.toString() + " + " + strategy2.toString();
    }

}
