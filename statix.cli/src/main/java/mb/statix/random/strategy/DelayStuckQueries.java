package mb.statix.random.strategy;

import java.util.Optional;

import org.metaborg.core.MetaborgRuntimeException;
import org.metaborg.util.functions.Predicate2;

import com.google.common.collect.Maps;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.IUnifier;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CResolveQuery;
import mb.statix.random.SearchContext;
import mb.statix.random.SearchState;
import mb.statix.random.SearchStrategy;
import mb.statix.random.nodes.SearchNode;
import mb.statix.random.nodes.SearchNodes;
import mb.statix.random.scopegraph.DataWF;
import mb.statix.random.scopegraph.NameResolution;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.scopegraph.reference.IncompleteDataException;
import mb.statix.scopegraph.reference.IncompleteEdgeException;
import mb.statix.scopegraph.reference.LabelOrder;
import mb.statix.scopegraph.reference.LabelWF;
import mb.statix.scopegraph.reference.ResolutionException;
import mb.statix.scopegraph.terms.Scope;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.query.RegExpLabelWF;
import mb.statix.solver.query.RelationLabelOrder;

final class DelayStuckQueries extends SearchStrategy<SearchState, SearchState> {

    @Override protected SearchNodes<SearchState> doApply(SearchContext ctx, SearchState input, SearchNode<?> parent) {
        final IState.Immutable state = input.state();
        final ICompleteness.Immutable completeness = input.completeness();

        final java.util.Map<IConstraint, Delay> delays = Maps.newHashMap();
        input.constraints().stream().filter(c -> c instanceof CResolveQuery).map(c -> (CResolveQuery) c).forEach(q -> {
            checkDelay(q, state, completeness).ifPresent(d -> {
                delays.put(q, d);
            });
        });

        final SearchState newState = input.delay(delays.entrySet());
        final String desc = this.toString() + "[" + delays.size() + "]";
        return SearchNodes.of(parent, new SearchNode<>(ctx.nextNodeId(), newState, parent, desc));
    }

    private Optional<Delay> checkDelay(CResolveQuery query, IState.Immutable state,
            ICompleteness.Immutable completeness) {
        final IUnifier unifier = state.unifier();

        if(!unifier.isGround(query.scopeTerm())) {
            return Optional.of(Delay.ofVars(unifier.getVars(query.scopeTerm())));
        }
        final Scope scope = Scope.matcher().match(query.scopeTerm(), unifier).orElse(null);
        if(scope == null) {
            return Optional.empty();
        }

        final Boolean isAlways;
        try {
            isAlways = query.min().getDataEquiv().isAlways(state.spec()).orElse(null);
        } catch(InterruptedException e) {
            throw new MetaborgRuntimeException(e);
        }
        if(isAlways == null) {
            return Optional.empty();
        }

        final Predicate2<Scope, ITerm> isComplete2 = (s, l) -> completeness.isComplete(s, l, state.unifier());
        final LabelWF<ITerm> labelWF = RegExpLabelWF.of(query.filter().getLabelWF());
        final LabelOrder<ITerm> labelOrd = new RelationLabelOrder(query.min().getLabelOrder());
        final DataWF<ITerm, CEqual> dataWF = new ResolveDataWF(state, completeness, query.filter().getDataWF(), query);

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
            throw new MetaborgRuntimeException("Unexpected resolution exception.", e);
        } catch(InterruptedException e) {
            throw new MetaborgRuntimeException(e);
        }

        return Optional.empty();
    }

    @Override public String toString() {
        return "delay-stuck-queries";
    }

}