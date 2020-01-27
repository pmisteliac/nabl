package mb.statix.generator.search;

import mb.statix.solver.IConstraint;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * Selects a constraint to be the focus of the subsequent strategies.
 *
 * This strategy focuses on the first constraint for which the selector succeeds,
 * or the strategy fails if there is none.
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
        Stream<StrategySearchState> newStates = input.getStates().flatMap(ss -> {
            @Nullable IConstraint focusConstraint = findFocusOrNull(ss);
            if (focusConstraint == null) return Stream.empty();

            return Stream.of(new StrategySearchState(ss.state(), ss.constraints(), focusConstraint, ss.delays(),
                    ss.existentials(), ss.completeness()));
        });
        return StrategyNode.of(newStates);
    }

    @Nullable private IConstraint findFocusOrNull(StrategySearchState ss) {
        for (IConstraint c : ss.constraints()) {
            if (selector.test(c)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "focus(?)";
    }

}
