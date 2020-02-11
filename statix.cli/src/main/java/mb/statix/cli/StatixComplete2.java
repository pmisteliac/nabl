package mb.statix.cli;

import com.google.common.collect.ImmutableList;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.codecompletion.TermCompleter;
import mb.statix.codecompletion.TermCompleter2;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.shell.StatixGenerator;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.statix.search.strategies.SearchStrategies.infer;


public final class StatixComplete2 {

    private static final ILogger log = LoggerUtils.logger(StatixComplete2.class);

    private final Statix STX;
    private final Spoofax spoofax;
    private final IContext context;

    public StatixComplete2(Statix stx, Spoofax spoofax, IContext context) {
        this.STX = stx;
        this.spoofax = spoofax;
        this.context = context;
    }

    public void run(String file) throws MetaborgException, InterruptedException {
        log.info("Reading " + file + "...");
        final FileObject resource = this.spoofax.resolve(file);
        Function1<ITerm, String> pretty = getSearchStatePrinter(resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();
        SearchContext ctx = new SearchContext(spec);
        SearchState state = prepare(ctx, spec, statixGen.constraint());
        if (state == null) {
            log.error("Aborted.");
            return;
        }

        long startTime = System.nanoTime();
        try {
            final TermCompleter2 completer = new TermCompleter2(spec);
            log.info("Completing...");
            List<ITerm> proposals = completer.complete(ctx, state, "e");
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            log.info("Completed to {} alternatives in {} s:", proposals.size(), String.format("%.3f", elapsedTime / 1000000000.0));
            proposals.forEach(t -> {
                System.out.println(pretty.apply(t));
            });
            log.info("Done.");
        } catch (RuntimeException e) {
            log.error("Completion failed: " + e.getMessage());
        }
    }

    private Function1<ITerm, String> getSearchStatePrinter(FileObject resource) {
        TermFormatter tf = ITerm::toString;
        try {
            final ILanguageImpl lang = STX.cli.loadLanguage(STX.project.location());
            final IContext context = STX.S.contextService.get(resource, STX.project, lang);
            tf = StatixGenerator.pretty(STX.S, context, "pp-generated");
        } catch(MetaborgException e) {
            // ignore
        }
        return tf::format;
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

//    private static ITerm project(String varName, SearchState s) {
//        final ITermVar v = B.newVar("", varName);
//        if(s.getExistentials() != null && s.getExistentials().containsKey(v)) {
//            return s.getState().unifier().findRecursive(s.getExistentials().get(v));
//        } else {
//            return v;
//        }
//    }


    /**
     * Performs inference on the input.
     * This must succeed or the original input constraint is not valid.
     *
     * Note that in the final implementation the initial state is provided by Statix,
     * after doing a normal analysis of the input file. For now, here we have to do
     * this manually. We do it here to avoid counting it toward the solving time.
     *
     * @param context the search context
     * @param spec the specification
     * @param constraint the input constraint
     * @return the resulting search state, from which completion may commence
     */
    private SearchState prepare(SearchContext context, Spec spec, IConstraint constraint) throws InterruptedException {
        log.info("Preparing...");
        SearchState initialState = SearchState.of(spec, State.of(spec), ImmutableList.of(constraint));
        Optional<SearchState> state = infer().apply(context, initialState).findFirst();
        if (!state.isPresent()) {
            log.error("Input program validation failed.");
            return null;
        }
        log.info("Ready.");
        return state.get();
    }

}