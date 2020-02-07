package mb.statix.search.strategies;

import mb.statix.search.*;
import mb.statix.sequences.Sequence;


/**
 * The ! strategy, which always succeeds and cuts the branches.
 */
public final class CutStrategy<T, CTX> implements Strategy<T, T, CTX> {

    private Sequence<?> branches;

    /**
     * Initializes a new instance of the {@link CutStrategy} class.
     * @param branches the branches to cut when this is evaluated
     */
    public CutStrategy(Sequence<?> branches) {
        this.branches = branches;
    }

    @Override
    public Sequence<T> apply(CTX ctx, T input) throws InterruptedException {
        this.branches.cut();
        return Sequence.of(input);
    }

    @Override
    public String toString() {
        return "!";
    }

}
