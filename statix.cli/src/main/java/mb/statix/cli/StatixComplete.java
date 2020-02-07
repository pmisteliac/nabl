package mb.statix.cli;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.codecompletion.TermCompleter;
import mb.statix.generator.SearchState;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
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
import java.util.stream.Collectors;

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
        Function1<SearchState, String> pretty = getSearchStatePrinter(resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();

        long startTime = System.nanoTime();
        final TermCompleter completer = new TermCompleter(spec);
        log.info("Completing...");
        List<SearchNode<SearchState>> nodes = completer.completeNodes(statixGen.constraint()).nodes().collect(Collectors.toList());
//        List<SearchState> proposals = completer.complete(statixGen.constraint());
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        log.info("Completed to {} alternatives in {} s:", nodes.size(), String.format("%.3f", elapsedTime / 1000000000.0));
        nodes.forEach(n -> {
            System.out.println("* " + n.desc());
            System.out.println("  " + pretty.apply(n.output()));
        });
//        proposals.forEach(s -> {
//            System.out.println(pretty.apply(s));
//        });
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
        if(s.existentials() != null && s.existentials().containsKey(v)) {
            return s.state().unifier().findRecursive(s.existentials().get(v));
        } else {
            return v;
        }
    }

}