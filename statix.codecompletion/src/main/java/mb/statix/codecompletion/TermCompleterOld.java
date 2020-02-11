package mb.statix.codecompletion;

import com.google.common.collect.ImmutableList;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.generator.DefaultSearchContext;
import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;

import java.util.List;
import java.util.stream.Collectors;

import static mb.statix.generator.strategy.SearchStrategies.*;

/**
 * The term completer.
 */
public final class TermCompleterOld {

    /** The Statix specification. */
    private final Spec spec;

    private static SearchStrategy<SearchState, SearchState> completionStrategy =
    // @formatter:off
        seq(infer())
         .$(limit(1, select(CUser.class)))
         .$(expand())
         .$(infer())
         .$(delayStuckQueries())
         .$(fix2(seq(limit(1, select(CResolveQuery.class)))
            .$(resolve())
            .$(infer())
            .$(delayStuckQueries())
            .$()
         ))
         .$();
    // @formatter:on

    /**
     * Initializes a new instance of the {@link TermCompleterOld} class.
     *
     * @param spec the Statix specification
     */
    public TermCompleterOld(Spec spec) {
        this.spec = spec;
    }

    /**
     * Completes the specified constraint.
     *
     * @param constraint the constraint to complete
     * @return the resulting states
     */
    public List<SearchState> complete(IConstraint constraint) {
        return completeNodes(constraint).nodes().map(SearchNode::output).collect(Collectors.toList());
    }

    /**
     * Completes the specified constraint.
     *
     * @param constraint the constraint to complete
     * @return the resulting states
     */
    public SearchNodes<SearchState> completeNodes(IConstraint constraint) {
        SearchContext ctx = new DefaultSearchContext(this.spec);
        SearchState initialState = SearchState.of(this.spec, State.of(this.spec), ImmutableList.of(constraint));
        SearchNode<SearchState> initialNode = new SearchNode<>(ctx.nextNodeId(), initialState, null, "init");
        return completionStrategy.apply(ctx, initialNode);
    }

}
