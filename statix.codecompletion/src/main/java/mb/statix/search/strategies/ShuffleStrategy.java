package mb.statix.search.strategies;

import mb.statix.search.RandomUtils;
import mb.statix.sequences.Sequence;
import mb.statix.search.Strategy;

import java.util.*;


/**
 * The shuffle(rng, s) strategy, which applies s, then shuffles the results.
 */
public final class ShuffleStrategy<A, B, CTX> implements Strategy<A, B, CTX> {

    private final Random rng;
    private final Strategy<A, B, CTX> strategy;

    public ShuffleStrategy(Random rng, Strategy<A, B, CTX> strategy) {
        this.rng = rng;
        this.strategy = strategy;
    }

    @Override
    public Sequence<B> apply(CTX ctx, A input) throws InterruptedException {
        return this.strategy.apply(ctx, input).transform(l -> {
            List<B> list = new ArrayList<>(l);
            Collections.shuffle(list, this.rng);
            return list;
        });
    }

    @Override
    public String toString() {
        return "shuffle(" + RandomUtils.getSeed(rng) + ", " + strategy.toString() + ")";
    }

}
