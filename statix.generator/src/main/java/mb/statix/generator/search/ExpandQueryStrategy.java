package mb.statix.generator.search;


import com.google.common.collect.*;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.IListTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CInequal;
import mb.statix.constraints.CResolveQuery;
import mb.statix.generator.scopegraph.DataWF;
import mb.statix.generator.scopegraph.Env;
import mb.statix.generator.scopegraph.Match;
import mb.statix.generator.scopegraph.NameResolution;
import mb.statix.generator.strategy.ResolveDataWF;
import mb.statix.generator.util.Subsets;
import mb.statix.scopegraph.reference.CriticalEdge;
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
import mb.statix.spec.Spec;
import mb.statix.spoofax.StatixTerms;
import org.metaborg.util.functions.Function2;
import org.metaborg.util.functions.Predicate0;
import org.metaborg.util.functions.Predicate2;
import org.metaborg.util.iterators.Iterables2;
import org.metaborg.util.optionals.Optionals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;
import static mb.statix.generator.util.StreamUtil.flatMap;


/**
 * Expands the focused query constraint.
 */
public final class ExpandQueryStrategy implements SStrategy {

    private final Function2<Integer, Integer, Boolean> selector;

    /**
     * Initializes a new instance of the {@link ExpandQueryStrategy} class.
     *
     * @param selector function (size, max) that given the size of the set and the max set size,
     *                 returns whether to include the set or not
     */
    public ExpandQueryStrategy(Function2<Integer, Integer, Boolean> selector) {
        this.selector = selector;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        Stream<StrategySearchState> newStates = input.getStates().flatMap(ss -> {
            IConstraint focusedConstraint = ss.getFocusedConstraint();
            if (!(focusedConstraint instanceof CResolveQuery)) return Stream.empty();
            CResolveQuery focus = (CResolveQuery)focusedConstraint;

            final IState.Immutable state = ss.state();
            final IUniDisunifier unifier = state.unifier();

            final Scope scope = getScope(focus, unifier);
            final boolean isAlways = isDataEquivalent(focus, context.getSpec());
            final ICompleteness.Immutable completeness = ss.completeness();
            final Predicate2<Scope, ITerm> isComplete2 = (s, l) -> completeness.isComplete(s, l, state.unifier());
            final LabelWF<ITerm> labelWF = RegExpLabelWF.of(focus.filter().getLabelWF());
            final LabelOrder<ITerm> labelOrd = new RelationLabelOrder(focus.min().getLabelOrder());
            final DataWF<ITerm, CEqual> dataWF =
                    new ResolveDataWF(context.getSpec(), state, completeness, focus.filter().getDataWF(), focus);

            // @formatter:off
            final NameResolution<Scope, ITerm, ITerm, CEqual> nameResolution = new NameResolution<>(
                    state.scopeGraph(), focus.relation(),
                    labelWF, labelOrd, isComplete2,
                    dataWF, isAlways, isComplete2);
            // @formatter:on

            final AtomicInteger count = new AtomicInteger(1);
            resolve(scope, nameResolution, () -> { count.incrementAndGet(); return false; });

            // We get a number of possible declarations the query could resolve to.
            // If we know that the ResultTerm is a singleton, then we know the query should resolve to a single declaration.
            // But if we don't know that the ResultTerm is a singleton, e.g., when it is a variable or wildcard,
            // then we also don't know how many declarations the query should resolve to (it might be limited by another
            // constraint to be a singleton set, but maybe not, and at this point we don't know).
            // Therefore, we make multiple possible resolutions: empty set (no declarations found), all the singleton
            // sets (resolve to one declaration), the set of all declarations, and random sets of combinations of declarations.
            // However, in this case we don't do random.
            // Example:
            //     let ɑ = ⋯
            //         β = ⋯
            //      in ɣ
            // Now a query in ɣ can resolve to {}, {ɑ}, {β}, {ɑ, β} unless we know it must be a singleton.
            // Essentially, when we're not doing random term generation we should return the powerset.
            // Next question is: how do we reduce the powerset in a meaningful way, such as selecting the empty
            // set, singleton sets, set of all, and some random ones in between? We could add a selection strategy.

            int resolutionCount = count.get();
            for (int i = 0; i < resolutionCount; i++) {
                final AtomicInteger select = new AtomicInteger(resolutionCount);
                final Env<Scope, ITerm, ITerm, CEqual> env = resolve(scope, nameResolution, () -> select.getAndDecrement() == 0);

                final List<Match<Scope, ITerm, ITerm, CEqual>> reqMatches =
                        env.matches.stream().filter(m -> !m.condition.isPresent()).collect(Collectors.toList());
                final List<Match<Scope, ITerm, ITerm, CEqual>> optMatches =
                        env.matches.stream().filter(m -> m.condition.isPresent()).collect(Collectors.toList());

                final Range<Integer> resultSize = resultSize(focus.resultTerm(), unifier, env.matches.size());
                final List<Integer> sizes = sizes(resultSize);

                return flatMap(sizes.stream().map(size -> size - reqMatches.size()), size -> {
                    return Subsets.of(optMatches).enumerate(size).map(entry -> {
                        final Env.Builder<Scope, ITerm, ITerm, CEqual> subEnvBuilder = Env.builder();
                        reqMatches.forEach(subEnvBuilder::match);
                        entry.getKey().forEach(subEnvBuilder::match);
                        entry.getValue().forEach(subEnvBuilder::reject);
                        env.rejects.forEach(subEnvBuilder::reject);
                        final Env<Scope, ITerm, ITerm, CEqual> subEnv = subEnvBuilder.build();
                        final List<ITerm> pathTerms = subEnv.matches.stream().map(m -> StatixTerms.explicate(m.path))
                                .collect(ImmutableList.toImmutableList());
                        final ImmutableList.Builder<IConstraint> constraints = ImmutableList.builder();
                        constraints.add(new CEqual(B.newList(pathTerms), focus.resultTerm(), focus));
                        flatMap(subEnv.matches.stream(), m -> Optionals.stream(m.condition)).forEach(condition -> {
                            constraints.add(condition);
                        });
                        flatMap(subEnv.rejects.stream(), m -> Optionals.stream(m.condition)).forEach(condition -> {
                            constraints.add(new CInequal(ImmutableSet.of(), condition.term1(), condition.term2(),
                                    condition.cause().orElse(null), condition.message().orElse(null)));
                        });
                        return update(ss, constraints.build(), Iterables2.singleton(focus));
                    });
                });
            }
            return null;

        });
        return StrategyNode.of(newStates);
    }

    private List<Integer> sizes(Range<Integer> resultSize) {
        final IntStream fixedSizes = IntStream.of(0, 1, resultSize.upperEndpoint());
        final IntStream allSizes = fixedSizes.filter(resultSize::contains).limit(3);
        return Lists.newArrayList(allSizes.boxed().collect(Collectors.toSet()));
    }

    /**
     * Update the constraints in this set, keeping completeness and delayed constraints in sync.
     *
     * This method assumes that no constraints appear in both add and remove, or it will be incorrect!
     */
    public StrategySearchState update(StrategySearchState ss, Iterable<IConstraint> add, Iterable<IConstraint> remove) {
        final ICompleteness.Transient completeness = ss.completeness().melt();
        final Set.Transient<IConstraint> constraints = ss.constraints().asTransient();
        final java.util.Set<CriticalEdge> removedEdges = Sets.newHashSet();
        add.forEach(c -> {
            if(constraints.__insert(c)) {
                completeness.add(c, ss.state().unifier());
            }
        });
        remove.forEach(c -> {
            if(constraints.__remove(c)) {
                removedEdges.addAll(completeness.remove(c, ss.state().unifier()));
            }
        });
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        ss.delays().forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });
        return new StrategySearchState(ss.state(), constraints.freeze(), null, delays.freeze(), ss.existentials(), completeness.freeze());
    }

    private Env<Scope, ITerm, ITerm, CEqual> resolve(Scope scope, NameResolution<Scope, ITerm, ITerm, CEqual> nameResolution, Predicate0 select) {
        final Env<Scope, ITerm, ITerm, CEqual> env;
        try {
            env = nameResolution.resolve(scope, select);
        } catch(ResolutionException e) {
            throw new IllegalArgumentException("cannot resolve query: delayed on " + e.getMessage());
        } catch(InterruptedException e) {
            // FIXME: This should be handled.
            throw new RuntimeException(e);
        }
        return env;
    }

    private Scope getScope(CResolveQuery focus, IUniDisunifier unifier) {
        final Scope scope = Scope.matcher().match(focus.scopeTerm(), unifier).orElse(null);
        if(scope == null) {
            throw new IllegalArgumentException("cannot resolve query: no scope");
        }
        return scope;
    }

    private boolean isDataEquivalent(CResolveQuery focus, Spec specification) {
        final Boolean isAlways;
        try {
            isAlways = focus.min().getDataEquiv().isAlways(specification).orElse(null);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(isAlways == null) {
            throw new IllegalArgumentException("cannot resolve query: cannot decide data equivalence");
        }
        return isAlways;
    }

//    private List<Integer> sizes(Range<Integer> resultSize) {
//        final IntStream fixedSizes = IntStream.of(0, 1, resultSize.upperEndpoint());
//        final IntStream randomSizes = RandomUtil.ints(2, resultSize.upperEndpoint(), rnd).limit(sizes);
//        final IntStream allSizes =
//                Streams.concat(fixedSizes, randomSizes).filter(size -> resultSize.contains(size)).limit(subsetsPerSize);
//        // make sure there are no duplicates, of resultSize.upperEndpoint equals one of the fixed values
//        final List<Integer> subsetSizes = Lists.newArrayList(allSizes.boxed().collect(Collectors.toSet()));
//        Collections.shuffle(subsetSizes, rnd);
//        return subsetSizes;
//    }

    private Range<Integer> resultSize(ITerm result, IUniDisunifier unifier, int max) {
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

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "expandQuery";
    }

}
