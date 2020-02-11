package mb.statix.codecompletion;

import com.google.common.collect.ImmutableList;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.util.TermFormatter;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.search.Strategy;
import mb.statix.solver.IConstraint;
import mb.statix.solver.SolverException;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.statix.search.strategies.SearchStrategies.*;
import static mb.statix.search.strategies.Strategies.*;


/**
 * The term completer.
 */
public final class TermCompleter2 {

    /** The Statix specification. */
    private final Spec spec;

    private static Strategy<SearchState, SearchState, SearchContext> completionStrategy =
    // @formatter:off
        seq(seq(limit(1, focus(CUser.class)))
             .$(expandRule())
             .$(infer())
             .$(delayStuckQueries())
             .$())
         .$(repeat(seq(limit(1, focus(CResolveQuery.class)))
            .$(expandQuery())
            .$(infer())
            .$(delayStuckQueries())
            .$()
         ))
         .$();
    // @formatter:on

    /**
     * Initializes a new instance of the {@link TermCompleter2} class.
     *
     * @param spec the Statix specification
     */
    public TermCompleter2(Spec spec) {
        this.spec = spec;
    }

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderName the name of the placeholder to complete
     * @return the resulting states
     */
    public List<ITerm> complete(SearchContext ctx, SearchState state, String placeholderName) throws InterruptedException {
        return completeNodes(ctx, state).map(s -> project(placeholderName, s)).collect(Collectors.toList());
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


    private String prettyPrint(IContext env, ITerm term) {
        IStrategoTerm strategoTerm = new StrategoTerms(env.getFactory()).toStratego(term);
        throw new RuntimeException("Not implemented");
    }

    private ITerm replaceVariablesWithPlaceholders(ITerm term) {
        throw new RuntimeException("Not implemented");
    }

    private ITerm replacePlaceholdersWithVariables(ITerm term) {
        throw new RuntimeException("Not implemented");
    }

//    private Function1<SearchState, String> getSearchStatePrinter(FileObject resource) {
//        TermFormatter tf = ITerm::toString;
//        try {
//            final ILanguageImpl lang = STX.cli.loadLanguage(STX.project.location());
//            final IContext context = STX.S.contextService.get(resource, STX.project, lang);
//            tf = StatixGenerator.pretty(STX.S, context, "pp-generated");
//        } catch(MetaborgException e) {
//            // ignore
//        }
//        final TermFormatter _tf = tf;
//        return (s) -> _tf.format(project("e", s));
//    }

    private static ITerm project(String varName, SearchState s) {
        final ITermVar v = B.newVar("", varName);
        if(s.getExistentials() != null && s.getExistentials().containsKey(v)) {
            return s.getState().unifier().findRecursive(s.getExistentials().get(v));
        } else {
            return v;
        }
    }
}
