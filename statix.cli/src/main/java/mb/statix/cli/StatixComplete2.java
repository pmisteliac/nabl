package mb.statix.cli;

import com.google.common.collect.ImmutableList;
import mb.nabl2.terms.ITerm;
import mb.nabl2.util.TermFormatter;
import mb.statix.codecompletion.CompletionProposal;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

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
        log.info("Preparing...");
        SearchState startState = SearchState.of(spec, State.of(spec), ImmutableList.of(statixGen.constraint()));
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        SearchState completionStartState = infer().apply(ctx, startState).findFirst().get();
        if (completionStartState.hasErrors()) {
            log.error("Input program validation failed. Aborted.\n" + completionStartState.toString());
            return;
        }
        if (completionStartState.getConstraints().isEmpty()) {
            log.error("No constraints left, nothing to complete. Aborted.\n" + completionStartState.toString());
            return;
        }
        log.info("Ready.");

        try {
            final TermCompleter2 completer = new TermCompleter2();
            log.info("Completing...");
            long startTime = System.nanoTime();
            List<CompletionProposal> proposals = completer.complete(ctx, completionStartState, "e");
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            log.info("Completed to {} alternatives in {} s:", proposals.size(), String.format("%.3f", elapsedTime / 1000000000.0));
            proposals.forEach(p -> {
                System.out.println(pretty.apply(p.getTerm()));
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

//    /**
//     * Performs inference on the input.
//     * This must succeed or the original input constraint is not valid.
//     *
//     * Note that in the final implementation the initial state is provided by Statix,
//     * after doing a normal analysis of the input file. For now, here we have to do
//     * this manually. We do it here to avoid counting it toward the solving time.
//     *
//     * @param context the search context
//     * @param spec the specification
//     * @param initialState the initial state
//     * @return the resulting search state, from which completion may commence;
//     * or {@code null} when preparation failed
//     */
//    @Nullable
//    private SearchState prepare(SearchContext context, Spec spec, SearchState initialState) throws InterruptedException {
//        return infer().apply(context, initialState).findFirst().orElse(null);
//    }

}