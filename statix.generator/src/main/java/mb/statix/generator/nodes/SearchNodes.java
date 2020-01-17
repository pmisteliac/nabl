package mb.statix.generator.nodes;

import java.util.stream.Stream;

import org.metaborg.util.functions.Function0;
import org.metaborg.util.functions.Function1;

import com.google.common.collect.ImmutableList;

import mb.statix.generator.SearchState;

import javax.annotation.Nullable;


/**
 * Represents a result of a search strategy, which is a lazy stream of search nodes.
 * When the stream is empty, the strategy failed.
 *
 * @param <O> the type of state in the search nodes
 */
public class SearchNodes<O extends SearchState> implements SearchElement {

    private final Stream<SearchNode<O>> nodes;
    private final Function0<String> desc;
    private final SearchNode<?> parent;

    private SearchNodes(Stream<SearchNode<O>> nodes, Function0<String> desc, @Nullable SearchNode<?> parent) {
        this.nodes = nodes;
        this.desc = desc;
        this.parent = parent;
    }

    /**
     * Gets the nodes in this node.
     *
     * @return a stream of nodes, which may be empty
     */
    public Stream<SearchNode<O>> nodes() {
        return nodes;
    }

    @Override @Nullable public SearchNode<?> parent() {
        return parent;
    }

    @Override public String desc() {
        return desc.apply();
    }

    @Override public String toString() {
        return desc();
    }

    // stream delegates

    public SearchNodes<O> limit(int n) {
        return new SearchNodes<>(nodes.limit(n), desc, parent);
    }

    public <R extends SearchState> SearchNodes<R> map(Function1<SearchNode<O>, SearchNode<R>> map) {
        return new SearchNodes<>(nodes.map(map::apply), desc, parent);
    }

    // construction methods

    /**
     * Creates a new instance of the {@link SearchNodes} class indicating that the search strategy failed.
     *
     * @param parent the parent node; or {@code null}
     * @param error the error message
     * @param <O> the type of state in the search nodes
     * @return the created object
     */
    public static <O extends SearchState> SearchNodes<O> failure(@Nullable SearchNode<?> parent, String error) {
        return new SearchNodes<>(Stream.empty(), () -> error, parent);
    }

    /**
     * Creates a new instance of the {@link SearchNodes} class with the specified list of nodes.
     *
     * @param parent the parent node; or {@code null}
     * @param error the lazy error message
     * @param nodes the nodes to include; or an empty iterable when the strategy failed
     * @param <O> the type of state in the search nodes
     * @return the created object
     */
    @SafeVarargs public static <O extends SearchState> SearchNodes<O> of(@Nullable SearchNode<?> parent, Function0<String> error,
            SearchNode<O>... nodes) {
        return new SearchNodes<>(ImmutableList.copyOf(nodes).stream(), error, parent);
    }

    /**
     * Creates a new instance of the {@link SearchNodes} class with the specified list of nodes.
     *
     * @param parent the parent node; or {@code null}
     * @param error the lazy error message
     * @param nodes a stream of nodes to include; or an empty stream when the strategy failed
     * @param <O> the type of state in the search nodes
     * @return the created object
     */
    public static <O extends SearchState> SearchNodes<O> of(@Nullable SearchNode<?> parent, Function0<String> error,
            Stream<SearchNode<O>> nodes) {
        return new SearchNodes<>(nodes, error, parent);
    }

}