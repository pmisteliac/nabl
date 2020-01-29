package mb.statix.search.strategies;

import com.google.common.collect.Sets;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.constraints.CUser;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.search.*;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;

import java.util.stream.Stream;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy implements Strategy<FocusedSearchState<CUser>, SearchState, SearchContext> {

    @Override
    public Sequence<SearchState> apply(SearchContext ctx, FocusedSearchState<CUser> state) throws InterruptedException {
//        CUser focus = state.getFocus();
//
//        final java.util.Set<Rule> rules = ctx.getSpec().rules().getIndependentRules(focus.name());
//        SearchState newState = RuleUtil.applyAll(state.state(), rules, focus.args(), focus)
//                .stream().map(t -> state.withApplyResult(focus, t._2()));
//        return Sequence.of(newState);
        return null;
    }

    @Override
    public String toString() {
        return "expandRule";
    }

}
