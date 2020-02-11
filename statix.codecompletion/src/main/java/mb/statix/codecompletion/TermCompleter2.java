package mb.statix.codecompletion;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Strategy;
import mb.statix.search.strategies.IdStrategy;
import mb.statix.search.strategies.SearchStrategies;
import mb.statix.search.strategies.Strategies;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.statix.search.strategies.SearchStrategies.*;
import static mb.statix.search.strategies.Strategies.*;


/**
 * The term completer.
 */
public final class TermCompleter2 {

    private static Strategy<SearchState, SearchState, SearchContext> completionStrategy =
    // @formatter:off
        seq(print(Strategies.<SearchState, SearchContext>id()))
         .$(seq(debug(limit(1, focus(CUser.class)), s -> System.out.println("Focused on: " + s)))
             .$(expandRule())
             .$(infer())
             .$(isSuccessful())
             .$(delayStuckQueries())
             .$())
         .$(repeat(seq(limit(1, focus(CResolveQuery.class)))
            .$(expandQuery())
            .$(infer())
            .$(isSuccessful())
            .$(delayStuckQueries())
            .$()
         ))
         .$();
    // @formatter:on

    /**
     * Initializes a new instance of the {@link TermCompleter2} class.
     */
    public TermCompleter2() {

    }

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderName the name of the placeholder to complete
     * @return the resulting completion proposals
     */
    public List<CompletionProposal> complete(SearchContext ctx, SearchState state, String placeholderName) throws InterruptedException {
        return completeNodes(ctx, state).map(s -> new CompletionProposal(project(placeholderName, s))).collect(Collectors.toList());
    }

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @return the resulting states
     */
    public Stream<SearchState> completeNodes(SearchContext ctx, SearchState state) throws InterruptedException {
        return completionStrategy.apply(ctx, state);
    }

    private static ITerm project(String varName, SearchState s) {
        final ITermVar v = B.newVar("", varName);
        if(s.getExistentials() != null && s.getExistentials().containsKey(v)) {
            return s.getState().unifier().findRecursive(s.getExistentials().get(v));
        } else {
            return v;
        }
    }
}
