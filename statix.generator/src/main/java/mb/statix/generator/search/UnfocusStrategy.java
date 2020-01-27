package mb.statix.generator.search;


import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Deselects any constraint from being the focus of the subsequent strategies.
 *
 * This strategy always succeeds.
 */
public final class UnfocusStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link UnfocusStrategy} class.
     */
    public UnfocusStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        Stream<StrategySearchState> newStates = input.getStates().map(ss -> {
            return new StrategySearchState(ss.state(), ss.constraints(), null, ss.delays(),
                    ss.existentials(), ss.completeness());
        });
        return StrategyNode.of(newStates);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "unfocus";
    }

}
