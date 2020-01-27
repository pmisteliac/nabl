package mb.statix.generator.search;

import com.google.common.collect.Maps;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CResolveQuery;
import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.scopegraph.DataWF;
import mb.statix.generator.scopegraph.NameResolution;
import mb.statix.generator.strategy.ResolveDataWF;
import mb.statix.scopegraph.reference.*;
import mb.statix.scopegraph.terms.Scope;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.query.RegExpLabelWF;
import mb.statix.solver.query.RelationLabelOrder;
import org.metaborg.util.functions.Predicate2;

import java.util.Optional;
import java.util.stream.Stream;


public final class DelayStuckQueriesStrategy implements SStrategy {

    @Override public StrategyNode apply(StrategyContext context, StrategyNode input) {
        return StrategyNode.of(input.getStates().flatMap(ss -> {
            final IState.Immutable state = ss.state();
            final ICompleteness.Immutable completeness = ss.completeness();

            final java.util.Map<IConstraint, Delay> delays = Maps.newHashMap();
            ss.constraints().stream().filter(c -> c instanceof CResolveQuery).map(c -> (CResolveQuery) c).forEach(q -> {
                checkDelay(context, q, state, completeness).ifPresent(d -> {
                    delays.put(q, d);
                });
            });

            final SearchState newState = ss.delay(delays.entrySet());
            final String desc = this.toString() + "[" + delays.size() + "]";
            return Stream.of(StrategySearchState.of(newState));
        }));
    }

    private Optional<Delay> checkDelay(StrategyContext ctx, CResolveQuery query, IState.Immutable state,
            ICompleteness.Immutable completeness) {
        final IUniDisunifier unifier = state.unifier();

        if(!unifier.isGround(query.scopeTerm())) {
            return Optional.of(Delay.ofVars(unifier.getVars(query.scopeTerm())));
        }
        final Scope scope = Scope.matcher().match(query.scopeTerm(), unifier).orElse(null);
        if(scope == null) {
            return Optional.empty();
        }

        final Boolean isAlways;
        try {
            isAlways = query.min().getDataEquiv().isAlways(ctx.getSpec()).orElse(null);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(isAlways == null) {
            return Optional.empty();
        }

        final Predicate2<Scope, ITerm> isComplete2 = (s, l) -> completeness.isComplete(s, l, state.unifier());
        final LabelWF<ITerm> labelWF = RegExpLabelWF.of(query.filter().getLabelWF());
        final LabelOrder<ITerm> labelOrd = new RelationLabelOrder(query.min().getLabelOrder());
        final DataWF<ITerm, CEqual> dataWF =
                new ResolveDataWF(ctx.getSpec(), state, completeness, query.filter().getDataWF(), query);

        // @formatter:off
        final NameResolution<Scope, ITerm, ITerm, CEqual> nameResolution = new NameResolution<>(
                state.scopeGraph(), query.relation(),
                labelWF, labelOrd, isComplete2,
                dataWF, isAlways, isComplete2);
        // @formatter:on

        try {
            nameResolution.resolve(scope, () -> false);
        } catch(IncompleteDataException e) {
            return Optional.of(Delay.ofCriticalEdge(CriticalEdge.of(e.scope(), e.relation())));
        } catch(IncompleteEdgeException e) {
            return Optional.of(Delay.ofCriticalEdge(CriticalEdge.of(e.scope(), e.label())));
        } catch(ResolutionException e) {
            throw new RuntimeException("Unexpected resolution exception.", e);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "delay-stuck-queries";
    }

}