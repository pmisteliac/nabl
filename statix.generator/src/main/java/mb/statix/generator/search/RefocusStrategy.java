package mb.statix.generator.search;

import io.usethesource.capsule.Set;
import mb.statix.solver.IConstraint;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Deselects any existing constraints and selects new constraints to be the focus of the subsequent strategies.
 */
public final class RefocusStrategy implements SStrategy {

    private final Predicate<IConstraint> selector;

    /**
     * Initializes a new instance of the {@link RefocusStrategy} class.
     *
     * @param selector the selector predicate
     */
    public RefocusStrategy(Predicate<IConstraint> selector) {
        this.selector = selector;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        StrategyNode result1 = new UnfocusStrategy(c -> true).apply(context, input);
        return new FocusStrategy(this.selector).apply(context, result1);
    }

}
