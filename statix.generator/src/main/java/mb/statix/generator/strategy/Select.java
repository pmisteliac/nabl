package mb.statix.generator.strategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.metaborg.util.functions.Function1;

import mb.statix.generator.FocusedSearchState;
import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.SearchStrategy.Mode;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.util.RandomGenerator;
import mb.statix.generator.util.StreamUtil;
import mb.statix.solver.IConstraint;
import mb.statix.spec.Spec;

final class Select<C extends IConstraint> extends SearchStrategy<SearchState, FocusedSearchState<C>> {

    private final Mode mode;
    private final Class<C> cls;
    private final Function1<SearchState, Function1<C, Double>> weight;

    Select(Spec spec, Mode mode, Class<C> cls, Function1<SearchState, Function1<C, Double>> weight) {
        super(spec);
        this.mode = mode;
        this.cls = cls;
        this.weight = weight;
    }

    @Override protected SearchNodes<FocusedSearchState<C>> doApply(SearchContext ctx, SearchNode<SearchState> node) {
        final SearchState input = node.output();
        final Function1<C, Double> w = weight.apply(input);
        final List<Pair<C, Double>> candidates = StreamUtil.filterInstances(cls, input.constraints().stream())
                .map(c -> new Pair<>(c, w.apply(c))).filter(p -> p.getValue() > 0).collect(Collectors.toList());
        if(candidates.isEmpty()) {
            return SearchNodes.failure(node, this.toString() + "[no candidates]");
        }
        
        final Stream<C> candidateNodes;
        switch(mode) {
	        case ENUM:
	        	candidateNodes = candidates.stream().map(p -> p.getKey());
	            break;
            case ENUM_SHUFFLED:
                Collections.shuffle(candidates, ctx.rnd());
                candidateNodes = candidates.stream().map(p -> p.getKey());
                break;
            case RND:
		        final EnumeratedDistribution<C> candidateDist =
		                new EnumeratedDistribution<>(new RandomGenerator(ctx.rnd()), candidates);
		        candidateNodes = StreamUtil.generate(candidateDist);
                break;
            default:
                throw new IllegalStateException();
        }

        Stream<SearchNode<FocusedSearchState<C>>> nodes = candidateNodes.map(c -> {
            final FocusedSearchState<C> output = FocusedSearchState.of(input, c);
            return new SearchNode<>(ctx.nextNodeId(), output, node,
                    "select(" + c.toString(t -> input.state().unifier().toString(t)) + ")");
        });
        final String desc = this.toString() + "[" + candidates.size() + "]";
        return SearchNodes.of(node, () -> desc, nodes);
    }

    @Override public String toString() {
        return "select(" + cls.getSimpleName() + ", " + weight.toString() + ")";
    }

}