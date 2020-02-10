package mb.statix.cli;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.codecompletion.TermCompleter;
import mb.statix.codecompletion.TermCompleter2;
import mb.statix.search.SearchState;
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

import static mb.nabl2.terms.build.TermBuild.B;

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
        Function1<SearchState, String> pretty = getSearchStatePrinter(resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();

        long startTime = System.nanoTime();
        final TermCompleter2 completer = new TermCompleter2(spec);
        log.info("Completing...");
        List<SearchState> proposals = completer.complete(statixGen.constraint());
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        log.info("Completed to {} alternatives in {} s:", proposals.size(), String.format("%.3f", elapsedTime / 1000000000.0));
        proposals.forEach(s -> {
            System.out.println(pretty.apply(s));
        });
        log.info("Done.");
    }

    private Function1<SearchState, String> getSearchStatePrinter(FileObject resource) {
        TermFormatter tf = ITerm::toString;
        try {
            final ILanguageImpl lang = STX.cli.loadLanguage(STX.project.location());
            final IContext context = STX.S.contextService.get(resource, STX.project, lang);
            tf = StatixGenerator.pretty(STX.S, context, "pp-generated");
        } catch(MetaborgException e) {
            // ignore
        }
        final TermFormatter _tf = tf;
        return (s) -> _tf.format(project("e", s));
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