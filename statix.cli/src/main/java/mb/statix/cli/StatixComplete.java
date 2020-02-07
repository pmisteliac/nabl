package mb.statix.cli;

import com.google.common.collect.Lists;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.codecompletion.TermCompleter;
import mb.statix.generator.RandomTermGenerator;
import mb.statix.generator.SearchLogger;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchElement;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.util.StreamProgressPrinter;
import mb.statix.solver.IConstraint;
import mb.statix.spec.Spec;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.shell.StatixGenerator;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.Level;
import org.metaborg.util.log.LoggerUtils;

import java.util.List;
import java.util.stream.Stream;

import static mb.nabl2.terms.build.TermBuild.B;


public final class StatixComplete {

    private static final ILogger log = LoggerUtils.logger(StatixComplete.class);

    private final Statix STX;
    private final Spoofax spoofax;
    private final IContext context;

    public StatixComplete(Statix stx, Spoofax spoofax, IContext context) {
        this.STX = stx;
        this.spoofax = spoofax;
        this.context = context;
    }

    public void run(String file) throws MetaborgException, InterruptedException {
        log.info("Reading " + file + "...");
        final FileObject resource = this.spoofax.resolve(file);
        Function1<mb.statix.search.SearchState, String> pretty = getSearchStatePrinter(resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();

        long startTime = System.nanoTime();
        final TermCompleter completer = new TermCompleter(spec);
        log.info("Completing...");
        List<mb.statix.search.SearchState> proposals = completer.complete(statixGen.constraint());
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        log.info("Completed to {} alternatives in {} s:", proposals.size(), String.format("%.3f", elapsedTime / 1000000000.0));
        proposals.forEach(s -> {
            System.out.println(pretty.apply(s));
        });
        log.info("Done.");
    }

    private Function1<mb.statix.search.SearchState, String> getSearchStatePrinter(FileObject resource) {
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

    private static ITerm project(String varName, mb.statix.search.SearchState s) {
        final ITermVar v = B.newVar("", varName);
        if(s.getExistentials() != null && s.getExistentials().containsKey(v)) {
            return s.getState().unifier().findRecursive(s.getExistentials().get(v));
        } else {
            return v;
        }
    }

}