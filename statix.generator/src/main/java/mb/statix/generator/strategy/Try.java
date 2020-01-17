package mb.statix.generator.strategy;

import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.util.StreamUtil;
import mb.statix.spec.Spec;
import java.util.stream.Stream;


/**
 * Attempts to apply a strategy. If the strategy fails, the original node is returned.
 */
public final class Try extends SearchStrategy<SearchState, SearchState> {

    private final SearchStrategy<SearchState, SearchState> s;

    public Try(Spec spec, SearchStrategy<SearchState, SearchState> s) {
        super(spec);
        this.s = s;
    }

    @Override protected SearchNodes<SearchState> doApply(SearchContext ctx, SearchNode<SearchState> node) {
        Stream<SearchNode<SearchState>> def = StreamUtil.orIfEmpty(s.apply(ctx, node).nodes(), () -> Stream.of(node));
        return SearchNodes.of(node.parent(), () -> "try(" + node.desc() + ")", def);
    }

    @Override public String toString() {
        return "try(" + this.s + ")";
    }

}