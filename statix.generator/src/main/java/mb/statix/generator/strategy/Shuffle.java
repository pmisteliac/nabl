package mb.statix.generator.strategy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.nabl2.util.Tuple2;
import mb.statix.constraints.CUser;
import mb.statix.generator.FocusedSearchState;
import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.util.RandomGenerator;
import mb.statix.generator.util.StreamUtil;
import mb.statix.scopegraph.reference.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;
import mb.statix.spec.Spec;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.metaborg.util.functions.Function2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Shuffles the nodes in the search graph.
 */
public final class Shuffle<I extends SearchState, O extends SearchState> extends SearchStrategy<I, O> {
    private final SearchStrategy<I, O> s;

    Shuffle(SearchStrategy<I, O> s) {
        this.s = s;
    }

    @Override protected SearchNodes<O> doApply(SearchContext ctx, SearchNode<I> node) {
        SearchNodes<O> result = this.s.apply(ctx, node);
        List<SearchNode<O>> nodes = result.nodes().collect(Collectors.toList());
        Collections.shuffle(nodes, ctx.rnd());
        Stream<SearchNode<O>> nodesStream = nodes.stream();

        final String desc = this.toString() + "[" + nodes.size() + "]";
        return SearchNodes.of(node, () -> desc, nodesStream);
    }

    @Override public String toString() {
        return "shuffle";
    }

}