package mb.statix.spec;

import com.google.common.collect.*;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.matching.Pattern;
import mb.nabl2.terms.substitution.IRenaming;
import mb.nabl2.terms.unification.OccursException;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.nabl2.terms.unification.ud.PersistentUniDisunifier;
import mb.nabl2.util.Tuple2;
import mb.statix.constraints.Constraints;
import mb.statix.solver.IConstraint;
import mb.statix.solver.StateUtil;

import java.util.List;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermPattern.P;


/**
 * A set of rules.
 */
public final class RuleSet {

    /** The rules, ordered from most specific o least specific guard. */
    private final ImmutableListMultimap<String, Rule> rules;

    /**
     * Initializes a new instance of the {@link RuleSet} class.
     *
     * @param rules the multimap of rule names to rules, ordered from most specific to least specific guard
     */
    public RuleSet(ImmutableListMultimap<String, Rule> rules) {
        this.rules = rules;
    }

    /**
     * Gets the names of all the rules in the ruleset.
     *
     * @return the set of rule names
     */
    public ImmutableSet<String> getRuleNames() {
        return this.rules.keySet();
    }

    /**
     * Gets all rules in the ruleset.
     *
     * @return all rules
     */
    public ImmutableCollection<Rule> getAllRules() {
        return this.rules.values();
    }

    /**
     * Gets the rules with the specified name in the ruleset.
     *
     * The rules are returned in order from most specific to least specific guard.
     *
     * @param name the name of the rules to find
     * @return the rules with the specified name
     */
    public ImmutableList<Rule> getRules(String name) {
        return this.rules.get(name);
    }

    /**
     * Gets a set of rules with the specified name, where the match order is reflected in (dis)equality constraints
     * in the rule bodies. The resulting rules can be applied independent of the other rules in the set.
     *
     * Note that compared to using applyAll, mismatches may only be discovered when the body of the returned rules is
     * evaluated, instead of during the matching process already.
     *
     * @param name the name of the rules to find
     * @return a set of rules that are mutually independent
     */
    public ImmutableSet<Rule> getIndependentRules(String name) {
        ImmutableList<Rule> rules = getRules(name);
        final List<Pattern> guards = Lists.newArrayList();

        return rules.stream().flatMap(r -> {
            final IUniDisunifier.Transient diseqs = PersistentUniDisunifier.Immutable.of().melt();

            // Eliminate wildcards in the patterns
            final FreshVars fresh = new FreshVars(r.varSet());
            final List<Pattern> paramPatterns = r.params().stream().map(p -> p.eliminateWld(() -> fresh.fresh("_")))
                    .collect(ImmutableList.toImmutableList());
            fresh.fix();
            final Pattern paramsPattern = P.newTuple(paramPatterns);

            // Create term for params and add implied equalities
            final Tuple2<ITerm, List<Tuple2<ITermVar, ITerm>>> p_eqs = paramsPattern.asTerm(v -> v.get());
            try {
                if(!diseqs.unify(p_eqs._2()).isPresent()) {
                    return Stream.empty();
                }
            } catch(OccursException e) {
                return Stream.empty();
            }

            // Add disunifications for all patterns from previous rules
            final boolean guardsOk = guards.stream().allMatch(g -> {
                final IRenaming swap = fresh.fresh(g.getVars());
                final Pattern g1 = g.eliminateWld(() -> fresh.fresh("_"));
                final Tuple2<ITerm, List<Tuple2<ITermVar, ITerm>>> t_eqs = g1.apply(swap).asTerm(v -> v.get());
                // Add internal equalities from the guard pattern, which are also reasons why the guard wouldn't match
                final List<ITermVar> leftEqs =
                        t_eqs._2().stream().map(Tuple2::_1).collect(ImmutableList.toImmutableList());
                final List<ITerm> rightEqs =
                        t_eqs._2().stream().map(Tuple2::_2).collect(ImmutableList.toImmutableList());
                final ITerm left = B.newTuple(p_eqs._1(), B.newTuple(leftEqs));
                final ITerm right = B.newTuple(t_eqs._1(), B.newTuple(rightEqs));
                final java.util.Set<ITermVar> universals = fresh.reset();
                return diseqs.disunify(universals, left, right).isPresent();
            });
            if(!guardsOk) return Stream.empty();

            // Add params as guard for next rule
            guards.add(paramsPattern);

            final IConstraint body = Constraints.conjoin(StateUtil.asInequalities(diseqs), r.body());
            return Stream.of(r.withParams(paramPatterns).withBody(body));
        }).collect(ImmutableSet.toImmutableSet());
    }

    /**
     * Gets a multimap from names to rules that have equivalent patterns.
     *
     * @return the map from names to equivalent rules
     */
    public ListMultimap<String, Rule> getAllEquivalentRules() {
        final ImmutableListMultimap.Builder<String, Rule> overlappingRules = ImmutableListMultimap.builder();
        this.rules.keySet().forEach(name -> overlappingRules.putAll(name, getEquivalentRules(name)));
        return overlappingRules.build();
    }

    /**
     * Gets a set of rules with equivalent patterns.
     *
     * @param name the name of the rules to find
     * @return a set of rules with equivalent patterns
     */
    public ImmutableSet<Rule> getEquivalentRules(String name) {
        ImmutableList<Rule> rules = getRules(name);
        return rules.stream().filter(a -> rules.stream().anyMatch(b -> !a.equals(b) && ARule.leftRightPatternOrdering.compare(a, b).map(c -> c == 0).orElse(false)))
                .collect(ImmutableSet.toImmutableSet());
    }

//    /**
//     * Make closed fragments from the given rules by inlining into the given rules. The predicates includePredicate and
//     * includeRule determine which premises should be inlined. The fragments are closed only w.r.t. the included
//     * predicates.
//     */
//    public ImmutableSet<Rule> getClosedFragmentRules(String name, Predicate1<String> includePredicate, Predicate2<String, String> includeRule, int generations) {
//
//        // 1. Make all rules unordered, and keep included rules
//        // @formatter:off
//        final ImmutableSetMultimap<String,Rule> rules = this.rules.keySet().stream()
//                .filter(includePredicate::test)
//                .flatMap(n -> getIndependentRules(n).stream())
//                .filter(r -> includeRule.test(r.name(), r.label()))
//                .collect(ImmutableSetMultimap.toImmutableSetMultimap(Rule::name, r -> r));
//        // @formatter:on
//
//        final PartialFunction1<IConstraint, CUser> expandable =
//                c -> (c instanceof CUser && rules.containsKey(((CUser) c).name())) ? Optional.of(((CUser) c))
//                        : Optional.empty();
//
//        // 2. Find the included axioms and move to fragments
//        // @formatter:off
//        final ImmutableSetMultimap<String,Rule> fragments = rules.entries().stream()
//                .filter(e -> Constraints.collectBase(expandable, false).apply(e.getValue().body()).isEmpty())
//                .collect(ImmutableSetMultimap.toImmutableSetMultimap(Map.Entry::getKey, Map.Entry::getValue));
//        // @formatter:on
//
//        fragments.forEach(rules::remove);       // FIXME
//
//        // 3. for each generation, inline fragments into rules
//        for(int g = 0; g < generations; g++) {
//            final SetMultimap<String, Rule> generation = HashMultimap.create();
//            rules.forEach((n, r) -> {
//                final FreshVars fresh = new FreshVars(r.varSet());
//                Constraints.flatMap(c -> {
//                    return Streams.stream(expandable.apply(c)).flatMap(u -> {
//                        return fragments.get(u.name()).stream().map(f -> applyToConstraint(fresh, f, u.args()));
//                    });
//                }, false).apply(r.body()) //
//                        .map(c -> r.withBody(c)) //
//                        .flatMap(f -> Streams.stream(simplify(f))) //
//                        .forEach(f -> generation.put(n, f));
//            });
//            fragments.putAll(generation);       // FIXME
//        }
//
//        return ImmutableSetMultimap.copyOf(fragments);
//    }

}
