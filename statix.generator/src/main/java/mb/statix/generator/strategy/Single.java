package mb.statix.generator.strategy;

import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import org.metaborg.util.functions.Function0;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Succeeds when there is a single node, fails otherwise.
 * @param <I>
 * @param <O>
 */
public final class Single<I extends SearchState, O extends SearchState> extends SearchStrategy<I, O> {
    private final SearchStrategy<I, O> s;

    Single(int n, SearchStrategy<I, O> s) {
        this.s = s;
    }

    @Override public SearchNodes<O> doApply(SearchContext ctx, SearchNode<I> node) {
        final SearchNodes<O> ns = s.apply(ctx, node);
        Function0<String> desc = () -> "single([" + ns.desc() + "])";
        List<SearchNode<O>> collected = ns.nodes().limit(2).collect(Collectors.toList());
        if (collected.size() == 1) {
            return SearchNodes.of(node, desc, Stream.of(collected.get(0)));
        } else {
            return SearchNodes.failure(node, "Not a single element");
        }
    }

    @Override public String toString() {
        return "single(" + s.toString() + ")";
    }

}