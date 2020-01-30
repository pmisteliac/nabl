package mb.statix.search.strategies;

import com.google.common.collect.ImmutableSet;
import mb.statix.constraints.CUser;
import mb.statix.search.*;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy implements Strategy<FocusedSearchState<CUser>, SearchState, SearchContext> {

    @Override
    public Sequence<SearchState> apply(SearchContext ctx, FocusedSearchState<CUser> state) throws InterruptedException {
        CUser focus = state.getFocus();

        final ImmutableSet<Rule> rules = ctx.getSpec().rules().getOrderIndependentRules(focus.name());
        SearchState searchState = state.getSearchState();
        return Sequence.from(RuleUtil.applyAll(searchState.getState(), rules, focus.args(), focus))
                .map(t -> searchState.withApplyResult(t._2(), focus));
    }

    @Override
    public String toString() {
        return "expandRule";
    }

}
