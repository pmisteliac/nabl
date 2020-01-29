package mb.statix.search.strategies;

import mb.statix.search.Sequence;
import mb.statix.search.Strategy;

import java.util.function.Consumer;


/**
 * The debug(s, a) strategy wraps a strategy, evaluates it, and applies an action to each element.
 */
public final class DebugStrategy<I, O, CTX> implements Strategy<I, O, CTX> {

    private final Consumer<O> action;
    private final Strategy<I, O, CTX> strategy;

    public DebugStrategy(Strategy<I, O, CTX> strategy, Consumer<O> action) {
        this.strategy = strategy;
        this.action = action;
    }

    @Override
    public Sequence<O> apply(CTX ctx, I input) throws InterruptedException {
        // Note that buffer() forces evaluation of the sequence.
        // This has a performance implication, but is required for a better debugging experience.
        return this.strategy.apply(ctx, input).forEach(this.action).buffer();
    }

    @Override
    public String toString() {
        return "debug(" + strategy.toString() + ")";
    }

}
