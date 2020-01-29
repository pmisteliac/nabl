package mb.statix.generator.strategy;

import java.util.Map;

import org.metaborg.util.functions.Action1;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.functions.Function2;
import org.metaborg.util.functions.Predicate1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;

import mb.statix.constraints.CConj;
import mb.statix.constraints.CUser;
import mb.statix.constraints.Constraints;
import mb.statix.generator.EitherSearchState;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.SearchStrategy.Mode;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.solver.IConstraint;
import mb.statix.spec.Rule;
import org.metaborg.util.log.ILogger;


public final class SearchStrategies {

    // Methods return concrete types for easier IDE navigation. Use `Open Return Type` to go to implementation directly.

    public final <I extends SearchState, O extends SearchState> Limit<I, O> limit(int n, SearchStrategy<I, O> s) {
        return new Limit<>(n, s);
    }

    public final <I extends SearchState, O extends SearchState> For<I, O> _for(int n, SearchStrategy<I, O> s) {
        return new For<>(n, s);
    }

    public final <I extends SearchState, O extends SearchState> Repeat<I, O> repeat(SearchStrategy<I, O> s) {
        return new Repeat<>(s);
    }

    public final <I extends SearchState, O extends SearchState> Seq.Builder<I, O> seq(SearchStrategy<I, O> s) {
        return new Seq.Builder<>(s);
    }

    public final <I extends SearchState, O extends SearchState> Shuffle<I, O> shuffle(SearchStrategy<I, O> s) {
        return new Shuffle<>(s);
    }

    public final <I extends SearchState, O1 extends SearchState, O2 extends SearchState> ConcatAlt<I, O1, O2>
            concatAlt(SearchStrategy<I, O1> s1, SearchStrategy<I, O2> s2) {
        return new ConcatAlt<>(s1, s2);
    }


    @SafeVarargs public final <I extends SearchState, O extends SearchState> Concat<I, O>
            concat(SearchStrategy<I, O>... ss) {
        return concat(ImmutableList.copyOf(ss));
    }

    public final <I extends SearchState, O extends SearchState> Concat<I, O> concat(Iterable<SearchStrategy<I, O>> ss) {
        return new Concat<>(ss);
    }

    public final <I1 extends SearchState, I2 extends SearchState, O extends SearchState>
            SearchStrategy<EitherSearchState<I1, I2>, O> match(SearchStrategy<I1, O> s1, SearchStrategy<I2, O> s2) {
        // this doesn't interleave!
        return new Match<>(s1, s2);
    }

    public final <I extends SearchState, O extends SearchState> Single<I, O> single(SearchStrategy<I, O> ss) {
        return new Single<>(ss);
    }

    public final Infer infer() {
        return new Infer();
    }

    public final Fix fix(SearchStrategy<SearchState, SearchState> search,
            SearchStrategy<SearchState, SearchState> infer, Predicate1<CUser> done, int maxConsecutiveFailures) {
        return new Fix(search, infer, c -> c instanceof CUser && done.test((CUser)c), maxConsecutiveFailures);
    }
    public final Fix fix2(SearchStrategy<SearchState, SearchState> search,
            SearchStrategy<SearchState, SearchState> infer, Predicate1<IConstraint> done, int maxConsecutiveFailures) {
        return new Fix(search, infer, done, maxConsecutiveFailures);
    }

    public final <I extends SearchState> Try<I> try_(SearchStrategy<I, I> search) {
        return new Try<>(search);
    }

    public final <C extends IConstraint> Select<C> select(Mode mode, Class<C> cls, Predicate1<C> include) {
        // full classes instead of lambda's to add forwarding toString
        return new Select<>(mode, cls, new Function1<SearchState, Function1<C, Double>>() {

            @Override public Function1<C, Double> apply(SearchState t) {
                return new Function1<C, Double>() {

                    @Override public Double apply(C c) {
                        return include.test(c) ? 1d : 0d;
                    }

                    @Override public String toString() {
                        return include.toString();
                    }

                };

            }

            @Override public String toString() {
                return include.toString();
            }

        });
    }

    public final <C extends IConstraint> Select<C> select(Mode mode, Class<C> cls,
            Function1<SearchState, Function1<C, Double>> weight) {
        return new Select<>(mode, cls, weight);
    }

    public final FilterConstraints filter(Predicate1<IConstraint> p) {
        return new FilterConstraints(p);
    }

    public final MapConstraints map(Function1<IConstraint, IConstraint> f) {
        return new MapConstraints(f);
    }

    public final Expand expand(Mode mode) {
        return expand(mode, 1d, ImmutableMap.of());
    }

    public final Expand expand(Mode mode, SetMultimap<String, Rule> rules) {
        return expand(mode, 1d, ImmutableMap.of(), rules);
    }

    public final Expand expand(Mode mode, double defaultWeight, Map<String, Double> weights) {
        return expand(mode, (r, n) -> {
            if(weights.containsKey(r.label())) {
                return weights.get(r.label()) / (double) n;
            } else {
                return defaultWeight;
            }
        });
    }

    public final Expand expand(Mode mode, double defaultWeight, Map<String, Double> weights,
            SetMultimap<String, Rule> rules) {
        return expand(mode, (r, n) -> {
            if(weights.containsKey(r.label())) {
                return weights.get(r.label()) / (double) n;
            } else {
                return defaultWeight;
            }
        }, rules);
    }

    public final Expand expand(Mode mode, Function2<Rule, Long, Double> ruleWeight) {
        return new Expand(mode, ruleWeight, null);
    }

    public final Expand expand(Mode mode, Function2<Rule, Long, Double> ruleWeight, SetMultimap<String, Rule> rules) {
        return new Expand(mode, ruleWeight, rules);
    }

    public final Resolve resolve() {
        return new Resolve();
    }

    public final CanResolve canResolve() {
        return new CanResolve();
    }

    public final DelayStuckQueries delayStuckQueries() {
        return new DelayStuckQueries();
    }

    public final <I extends SearchState, O extends SearchState> Debug<I, O> debug(SearchStrategy<I, O> s,
            Action1<SearchNode<O>> debug) {
        return new Debug<>(debug, s);
    }

    public final <I extends SearchState, O extends SearchState> Debug<I, O> debugStates(SearchStrategy<I, O> s, ILogger log, String prefix) {
        return debug(s, n -> log.info(prefix + ": " + n.output().toString()));
    }

    public final <I extends SearchState> Identity<I> identity() {
        return new Identity<>();
    }

    public final <I extends SearchState> Mark<I> marker(String marker) {
        return new Mark<>(marker);
    }

    public final <I extends SearchState, O extends SearchState> Require<I, O> require(SearchStrategy<I, O> s) {
        return new Require<>(s);
    }

    // util

    public SearchStrategy<SearchState, SearchState> mapPred(String pattern, Function1<CUser, IConstraint> f) {
        final mb.statix.generator.predicate.Match match = new mb.statix.generator.predicate.Match(pattern);
        return map(Constraints.bottomup(Constraints.<IConstraint>cases().user(c -> {
            if(match.test(c)) {
                return f.apply(c);
            } else {
                return c;
            }
        }).otherwise(c -> {
            return c;
        }), false));
    }

    public SearchStrategy<SearchState, SearchState> addAuxPred(String pattern, Function1<CUser, IConstraint> f) {
        return mapPred(pattern, c -> {
            return new CConj(c, f.apply(c), c);
        });
    }

    public SearchStrategy<SearchState, SearchState> dropPred(String pattern) {
        final mb.statix.generator.predicate.Match match = new mb.statix.generator.predicate.Match(pattern);
        return filter(Constraints.<Boolean>cases().user(c -> !match.test(c)).otherwise(c -> true)::apply);
    }

    public SearchStrategy<SearchState, SearchState> dropAst() {
        return filter(
                Constraints.<Boolean>cases().termId(c -> false).termProperty(c -> false).otherwise(c -> true)::apply);
    }


}