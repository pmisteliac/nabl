package mb.statix.generator.search;


import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Deselects some constraints to be the focus of the subsequent strategies.
 *
 * This strategy removes from any existing focused constraints.
 */
public final class UnfocusStrategy implements SStrategy {

    private final Predicate<IConstraint> deselector;

    /**
     * Initializes a new instance of the {@link UnfocusStrategy} class.
     *
     * @param deselector the deselector predicate
     */
    public UnfocusStrategy(Predicate<IConstraint> deselector) {
        this.deselector = deselector;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        Stream<StrategySearchState> newStates = input.getStates().map(ss -> {
            Set.Transient<IConstraint> focusConstraints = ss.getFocusedConstraints().asTransient();
            focusConstraints.removeIf(this.deselector);
            return new StrategySearchState(ss.state(), ss.constraints(), focusConstraints.freeze(), ss.delays(),
                    ss.existentials(), ss.completeness());
        });
        return StrategyNode.of(newStates);
    }

}
