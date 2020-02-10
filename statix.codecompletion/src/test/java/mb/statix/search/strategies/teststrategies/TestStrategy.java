package mb.statix.search.strategies.teststrategies;

import mb.statix.search.Strategy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


/**
 * Test strategy that wraps another strategy and counts how many times it has been invoked.
 *
 * @param <I> the type of input
 * @param <O> the type of output
 * @param <CTX> the type of context
 */
public final class TestStrategy<I, O, CTX> implements Strategy<I, O, CTX> {

    /** The number of evaluations. */
    private final AtomicInteger evalCount = new AtomicInteger(0);
    /** The maximum number of evaluations; or -1 to impose no restrictions. */
    private final int maxEvalCount;
    /** The wrapped strategy. */
    private final Strategy<I, O, CTX> s;

    /**
     * Initializes a new instance of the {@link TestStrategy} class.
     *
     * @param s the strategy to wrap
     * @param maxEvalCount the maximum number of evaluations; or -1 to impose no restrictions
     */
    public TestStrategy(Strategy<I, O, CTX> s, int maxEvalCount) {
        this.s = s;
        this.maxEvalCount = maxEvalCount;
    }
    /**
     * Initializes a new instance of the {@link TestStrategy} class.
     *
     * @param s the strategy to wrap
     */
    public TestStrategy(Strategy<I, O, CTX> s) {
        this(s, -1);
    }

    @Override
    public Stream<O> apply(CTX ctx, I input) throws InterruptedException {
        return Stream.of(input).flatMap(i -> {
            if (!incEvalCount()) return Stream.empty();
            try {
                return s.apply(ctx, i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Gets the number of evaluations.
     *
     * @return the number of evaluations
     */
    public int getEvalCount() { return this.evalCount.get(); }

    /**
     * Attempts to increment the evaluation counter and returns whether to continue.
     *
     * @return {@code true} to continue; otherwise, {@code false}.
     */
    private boolean incEvalCount() {
        int evalCount = this.evalCount.get();
        if (maxEvalCount < 0 || evalCount < maxEvalCount) {
            this.evalCount.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }

}
