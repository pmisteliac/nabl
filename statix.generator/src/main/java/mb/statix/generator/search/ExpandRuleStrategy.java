package mb.statix.generator.search;


import com.google.common.collect.Sets;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.constraints.CUser;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;

import java.util.stream.Stream;


/**
 * Expands the focused user constraint.
 */
public final class ExpandRuleStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link ExpandRuleStrategy} class.
     */
    public ExpandRuleStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        Stream<StrategySearchState> newStates = input.getStates().flatMap(ss -> {
            IConstraint focusedConstraint = ss.getFocusedConstraint();
            if (!(focusedConstraint instanceof CUser)) return Stream.empty();
            CUser focus = (CUser)focusedConstraint;

            final java.util.Set<Rule> rules = context.getSpec().rules().getIndependentRules(focus.name());
            return RuleUtil.applyAll(ss.state(), rules, focus.args(), focus)
                    .stream().map(t -> updateSearchState(focus, t._2(), ss));
        });
        return StrategyNode.of(newStates);
    }

    private StrategySearchState updateSearchState(IConstraint predicate, ApplyResult result, StrategySearchState input) {
        final IConstraint applyConstraint = result.body();
        final IState.Immutable applyState = result.state();
        final IUniDisunifier.Immutable applyUnifier = applyState.unifier();

        // Update constraints
        final Set.Transient<IConstraint> constraints = input.constraints().asTransient();
        constraints.__insert(applyConstraint);
        constraints.__remove(predicate);

        // Update completeness
        final ICompleteness.Transient completeness = input.completeness().melt();
        completeness.updateAll(result.diff().varSet(), applyUnifier);
        completeness.add(applyConstraint, applyUnifier);
        java.util.Set<CriticalEdge> removedEdges = completeness.remove(predicate, applyUnifier);

        // Update delays
        final io.usethesource.capsule.Map.Transient<IConstraint, Delay> delays = io.usethesource.capsule.Map.Transient.of();
        input.delays().forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });

        // Return new state
        return new StrategySearchState(applyState, constraints.freeze(), null, delays.freeze(), input.existentials(), completeness.freeze());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "expandRule";
    }
}
