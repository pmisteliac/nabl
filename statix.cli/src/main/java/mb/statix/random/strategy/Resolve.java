package mb.statix.random.strategy;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.metaborg.core.MetaborgRuntimeException;
import org.metaborg.util.functions.Predicate2;
import org.metaborg.util.optionals.Optionals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Streams;

import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.unification.IUnifier;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CInequal;
import mb.statix.constraints.CResolveQuery;
import mb.statix.random.FocusedSearchState;
import mb.statix.random.SearchContext;
import mb.statix.random.SearchNode;
import mb.statix.random.SearchNodes;
import mb.statix.random.SearchState;
import mb.statix.random.SearchStrategy;
import mb.statix.random.scopegraph.DataWF;
import mb.statix.random.scopegraph.Env;
import mb.statix.random.scopegraph.Match;
import mb.statix.random.scopegraph.NameResolution;
import mb.statix.random.util.RandomUtil;
import mb.statix.random.util.Subsets;
import mb.statix.scopegraph.reference.LabelOrder;
import mb.statix.scopegraph.reference.LabelWF;
import mb.statix.scopegraph.reference.ResolutionException;
import mb.statix.scopegraph.terms.Scope;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.Completeness;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.completeness.IsComplete;
import mb.statix.solver.query.RegExpLabelWF;
import mb.statix.solver.query.RelationLabelOrder;
import mb.statix.spoofax.StatixTerms;

final class Resolve extends SearchStrategy<FocusedSearchState<CResolveQuery>, SearchState> {

    private final int sizes = 2;
    private final int subsetsPerSize = 3;

    @Override protected SearchNodes<SearchState> doApply(SearchContext ctx, FocusedSearchState<CResolveQuery> input,
            SearchNode<?> parent) {
        final IState.Immutable state = input.state();
        final IUnifier unifier = state.unifier();
        final CResolveQuery query = input.focus();

        final Scope scope = Scope.matcher().match(query.scopeTerm(), unifier).orElse(null);
        if(scope == null) {
            throw new IllegalArgumentException("cannot resolve query: no scope");
        }

        final Boolean isAlways;
        try {
            isAlways = query.min().getDataEquiv().isAlways(state.spec()).orElse(null);
        } catch(InterruptedException e) {
            throw new MetaborgRuntimeException(e);
        }
        if(isAlways == null) {
            throw new IllegalArgumentException("cannot resolve query: cannot decide data equivalence");
        }

        final ICompleteness.Transient completeness = Completeness.Transient.of(state.spec());
        completeness.addAll(input.constraints(), unifier);
        final IsComplete isComplete3 = (s, l, st) -> completeness.isComplete(s, l, st.unifier());
        final Predicate2<Scope, ITerm> isComplete2 = (s, l) -> completeness.isComplete(s, l, state.unifier());
        final LabelWF<ITerm> labelWF = RegExpLabelWF.of(query.filter().getLabelWF());
        final LabelOrder<ITerm> labelOrd = new RelationLabelOrder(query.min().getLabelOrder());
        final DataWF<ITerm, CEqual> dataWF = new ResolveDataWF(isComplete3, state, query.filter().getDataWF(), query);

        // @formatter:off
        final NameResolution<Scope, ITerm, ITerm, CEqual> nameResolution = new NameResolution<>(
                state.scopeGraph(), query.relation(),
                labelWF, labelOrd, isComplete2,
                dataWF, isAlways, isComplete2);
        // @formatter:on

        final AtomicInteger count = new AtomicInteger(1);
        try {
            nameResolution.resolve(scope, () -> {
                count.incrementAndGet();
                return false;
            });
        } catch(ResolutionException e) {
            throw new IllegalArgumentException("cannot resolve query: delayed on " + e.getMessage());
        } catch(InterruptedException e) {
            throw new MetaborgRuntimeException(e);
        }

        final List<Integer> indices =
                IntStream.range(0, count.get()).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(indices, ctx.rnd());

        return SearchNodes.of(indices.stream().flatMap(idx -> {
            final AtomicInteger select = new AtomicInteger(idx);
            final Env<Scope, ITerm, ITerm, CEqual> env;
            try {
                env = nameResolution.resolve(scope, () -> select.getAndDecrement() == 0);
            } catch(ResolutionException e) {
                throw new IllegalArgumentException("cannot resolve query: delayed on " + e.getMessage());
            } catch(InterruptedException e) {
                throw new MetaborgRuntimeException(e);
            }

            final List<Match<Scope, ITerm, ITerm, CEqual>> reqMatches =
                    env.matches.stream().filter(m -> !m.condition.isPresent()).collect(Collectors.toList());
            final List<Match<Scope, ITerm, ITerm, CEqual>> optMatches =
                    env.matches.stream().filter(m -> m.condition.isPresent()).collect(Collectors.toList());

            final Range<Integer> resultSize = resultSize(query.resultTerm(), unifier, env.matches.size());
            final List<Integer> sizes = sizes(resultSize, ctx.rnd());

            return sizes.stream().map(size -> size - reqMatches.size()).flatMap(size -> {
                return Subsets.of(optMatches).enumerate(size, ctx.rnd()).map(entry -> {
                    final Env.Builder<Scope, ITerm, ITerm, CEqual> subEnvBuilder = Env.builder();
                    reqMatches.forEach(subEnvBuilder::match);
                    entry.getKey().forEach(subEnvBuilder::match);
                    entry.getValue().forEach(subEnvBuilder::reject);
                    env.rejects.forEach(subEnvBuilder::reject);
                    final Env<Scope, ITerm, ITerm, CEqual> subEnv = subEnvBuilder.build();
                    final List<ITerm> pathTerms = subEnv.matches.stream().map(m -> StatixTerms.explicate(m.path))
                            .collect(ImmutableList.toImmutableList());
                    final ImmutableList.Builder<IConstraint> constraints = ImmutableList.builder();
                    constraints.add(new CEqual(B.newList(pathTerms), query.resultTerm(), query));
                    subEnv.matches.stream().flatMap(m -> Optionals.stream(m.condition))
                            .forEach(condition -> constraints.add(condition));
                    subEnv.rejects.stream().flatMap(m -> Optionals.stream(m.condition)).forEach(condition -> constraints
                            .add(new CInequal(condition.term1(), condition.term2(), condition.cause().orElse(null))));
                    constraints.addAll(input.constraints());
                    final SearchState newState = input.update(input.state(), constraints.build());
                    return new SearchNode<>(ctx.nextNodeId(), newState, parent,
                            "resolve[" + idx + "/" + count.get() + "]");
                });
            });
        }));
    }

    private List<Integer> sizes(Range<Integer> resultSize, Random rnd) {
        final IntStream fixedSizes = IntStream.of(0, 1, resultSize.upperEndpoint());
        final IntStream randomSizes = RandomUtil.ints(2, resultSize.upperEndpoint(), rnd).limit(sizes);
        final IntStream allSizes =
                Streams.concat(fixedSizes, randomSizes).filter(size -> resultSize.contains(size)).limit(subsetsPerSize);
        // make sure there are no duplicates, of resultSize.upperEndpoint equals one of the fixed values
        final List<Integer> subsetSizes = Lists.newArrayList(allSizes.boxed().collect(Collectors.toSet()));
        Collections.shuffle(subsetSizes, rnd);
        return subsetSizes;
    }

    private Range<Integer> resultSize(ITerm result, IUnifier unifier, int max) {
        // @formatter:off
        final AtomicInteger min = new AtomicInteger(0);
        return M.<Range<Integer>>list(ListTerms.<Range<Integer>>casesFix(
            (m, cons) -> {
                min.incrementAndGet();
                return m.apply((IListTerm) unifier.findTerm(cons.getTail()));
            },
            (m, nil) -> {
                return Range.singleton(min.get());
            },
            (m, var) -> {
                return Range.closed(min.get(), max);
            }
        )).match(result, unifier).orElse(Range.closed(0, max));
        // @formatter:on
    }

    @Override public String toString() {
        return "resolve";
    }

}