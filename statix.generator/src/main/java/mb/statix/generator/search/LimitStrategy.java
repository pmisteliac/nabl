package mb.statix.generator.search;


import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Limits the number of states.
 */
public final class LimitStrategy implements SStrategy {

    private final int limit;

    /**
     * Initializes a new instance of the {@link LimitStrategy} class.
     *
     * @param limit the limit
     */
    public LimitStrategy(int limit) {
        if (limit < 0) throw new IllegalArgumentException("limit must be greater than or equal to zero.");
        this.limit = limit;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        return StrategyNode.of(input.getStates().limit(this.limit));
    }

}
