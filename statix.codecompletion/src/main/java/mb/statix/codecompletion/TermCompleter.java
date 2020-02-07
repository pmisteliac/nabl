package mb.statix.codecompletion;

import com.google.common.collect.ImmutableList;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Sequence;
import mb.statix.search.Strategy;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;

import java.util.List;

import static mb.statix.search.strategies.Strategies.*;

/**
 * The term completer.
 */
public final class TermCompleter {

    /** The Statix specification. */
    private final Spec spec;

    private static Strategy<SearchState, SearchState, SearchContext> completionStrategy =
    // @formatter:off
        seq(infer())
         .$(limit(1, focus(CUser.class)))
         .$(expandRule())
         .$(infer())
         .$(repeat(seq(limit(1, focus(CResolveQuery.class)))
            .$(expandQuery())
            .$(infer())
            .$(delayStuckQueries())
            .$()
         ))
         .$();
    // @formatter:on

    /**
     * Initializes a new instance of the {@link TermCompleter} class.
     *
     * @param spec the Statix specification
     */
    public TermCompleter(Spec spec) {
        this.spec = spec;
    }

    /**
     * Completes the specified constraint.
     *
     * @param constraint the constraint to complete
     * @return the resulting states
     */
    public List<SearchState> complete(IConstraint constraint) throws InterruptedException {
        SearchContext ctx = new SearchContext(this.spec);
        SearchState initialState = SearchState.of(this.spec, State.of(this.spec), ImmutableList.of(constraint));
        Sequence<SearchState> results = completionStrategy.apply(ctx, initialState);
        return results.toList();
    }

}
