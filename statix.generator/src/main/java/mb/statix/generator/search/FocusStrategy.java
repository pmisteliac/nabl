package mb.statix.generator.search;


import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Selects some constraints to be the focus of the subsequent strategies.
 *
 * This strategy adds to any existing focused constraints. Use the unfocus strategy
 * to unfocus other constraints, or refocus to change focus.
 */
public final class FocusStrategy implements SStrategy {

    private final Predicate<IConstraint> selector;

    /**
     * Initializes a new instance of the {@link FocusStrategy} class.
     *
     * @param selector the selector predicate
     */
    public FocusStrategy(Predicate<IConstraint> selector) {
        this.selector = selector;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        Stream<StrategySearchState> newStates = input.getStates().map(ss -> {
            Set.Transient<IConstraint> focusConstraints = ss.constraints().asTransient();
            focusConstraints.removeIf(c -> !this.selector.test(c));
            focusConstraints.__insertAll(ss.getFocusedConstraints());
            return new StrategySearchState(ss.state(), ss.constraints(), focusConstraints.freeze(), ss.delays(),
                    ss.existentials(), ss.completeness());
        });
        return StrategyNode.of(newStates);
    }

}
