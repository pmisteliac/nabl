package mb.statix.search.strategies;

import mb.statix.search.Strategy;

import java.util.stream.Stream;

import static mb.statix.search.strategies.Strategies.*;


/**
 * The repeat(s) strategy, which applies s until it fails.
 */
public final class RepeatStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private final Strategy<T, T, CTX> s;

    public RepeatStrategy(Strategy<T, T, CTX> s) {
        this.s = s;
    }

    @Override
    public Stream<T> apply(CTX ctx, T input) throws InterruptedException {
        return try_(seq(s).$(repeat(s)).$()).apply(ctx, input);
    }

    @Override
    public String toString() {
        return "repeat(" + s.toString() + ")";
    }

}
