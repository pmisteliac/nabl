package mb.statix.search.strategies;

import mb.statix.search.*;

import java.util.List;


/**
 * The s1; s2 strategy.
 */
public final class SeqStrategy<A, B, C, CTX> implements Strategy<A, C, CTX> {

    private final Strategy<A, B, CTX> strategy1;
    private final Strategy<B, C, CTX> strategy2;

    public SeqStrategy(Strategy<A, B, CTX> strategy1, Strategy<B, C, CTX> strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    @Override
    public Sequence<C> apply(CTX ctx, A input){
        return this.strategy1.apply(ctx, input).flatMap(n -> this.strategy2.apply(ctx, n));
    }

    @Override
    public String toString() {
        return "(" + strategy1.toString() + "; " + strategy2.toString() + ")";
    }

}
