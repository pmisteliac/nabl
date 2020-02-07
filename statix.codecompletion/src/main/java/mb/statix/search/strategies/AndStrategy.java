package mb.statix.search.strategies;

import mb.statix.sequences.Sequence;
import mb.statix.search.Strategy;


/**
 * The s1; s2 strategy.
 */
public final class AndStrategy<A, B, C, CTX> implements Strategy<A, C, CTX> {

    private final Strategy<A, B, CTX> strategy1;
    private final Strategy<B, C, CTX> strategy2;

    public AndStrategy(Strategy<A, B, CTX> strategy1, Strategy<B, C, CTX> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Sequence<C> apply(CTX ctx, A input) throws InterruptedException {
        return this.strategy1.apply(ctx, input).flatMap(n -> {
            try {
                return this.strategy2.apply(ctx, n);
            } catch (InterruptedException e) {
                // Very annoying that we have to do this
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String toString() {
        return "(" + strategy1.toString() + "; " + strategy2.toString() + ")";
    }

}
