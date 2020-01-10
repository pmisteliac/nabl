package mb.statix.cli;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.completions.TermCompleter;
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
import org.metaborg.core.action.TransformActionContrib;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.shell.StatixGenerator;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.Level;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import static mb.nabl2.terms.build.TermBuild.B;


public class StatixComplete {

    private static final ILogger log = LoggerUtils.logger(StatixTest.class);

    private static final String VAR = "e";

    private final Statix STX;
    private final Spoofax spoofax;
    private final IContext context;

    public StatixComplete(Statix stx, Spoofax spoofax, IContext context) {
        this.STX = stx;
        this.spoofax = spoofax;
        this.context = context;
    }

    public void run(String file) throws MetaborgException {
        log.info("Reading " + file + "...");
        final FileObject resource = this.spoofax.resolve(file);
        final Function1<SearchState, String> pretty = getSearchStatePrinter(resource);
        final CSearchLogger searchLog = new CSearchLogger(pretty);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();
        final TermCompleter com = new TermCompleter(spec, statixGen.constraint(), new Paret(spec).search(), searchLog);
        final Stream<SearchState> resultStream = com.apply().nodes().map(sn -> {
            searchLog.success(sn);
            return sn.output();
        });

        log.info("Completing...");
        final List<SearchState> results = Lists.newArrayList(resultStream.iterator());
        searchLog.progress.done();
        log.info("Completed to {} alternatives:", results.size());
        results.forEach(s -> {
            System.out.println(pretty.apply(s));
        });
        log.info("Done.");
    }






    private Function1<SearchState, String> getSearchStatePrinter(FileObject resource) {
        TermFormatter tf = ITerm::toString;
        try {
            final ILanguageImpl lang = STX.cli.loadLanguage(STX.project.location());
            final IContext context = this.spoofax.contextService.get(resource, STX.project, lang);
            tf = StatixGenerator.pretty(this.spoofax, context, "pp-generated");
        } catch(MetaborgException e) {
            // ignore
        }
        final TermFormatter _tf = tf;
        return (s) -> _tf.format(project(VAR, s));
    }

    private static ITerm project(String varName, SearchState s) {
        final ITermVar v = B.newVar("", varName);
        if(s.existentials().containsKey(v)) {
            return s.state().unifier().findRecursive(s.existentials().get(v));
        } else {
            return v;
        }
    }

    private class CSearchLogger implements SearchLogger  {
        public final DescriptiveStatistics hitStats = new DescriptiveStatistics();
        public final DescriptiveStatistics missStats = new DescriptiveStatistics();
        public final Function1<SearchState, String> pretty;

        public final StreamProgressPrinter progress = new StreamProgressPrinter(System.err, 80, out -> {
            long hits = hitStats.getN();
            long all = hits + missStats.getN();
            out.println(" " + hits + "/" + all + " " + summary(hitStats));
        });

        public CSearchLogger(Function1<SearchState, String> pretty) {
            this.pretty = pretty;
        }

        @Override public void init(long seed, SearchStrategy<?, ?> strategy, Iterable<IConstraint> constraints) {
            log.info("seed {}", seed);
            log.info("strategy {}", strategy);
            log.info("constraints {}", constraints);
        }

        @Override public void success(SearchNode<SearchState> n) {
            progress.step('+');
            addSize(n, hitStats);
            log.log(Level.Debug, "=== SUCCESS ===");
            log.log(Level.Debug, " * {}", pretty.apply(n.output()));
            log.log(Level.Debug, "---- Trace ----");
            logTrace(n, 1);
            log.log(Level.Debug, "===============");
        }

        @Override public void failure(SearchNodes<?> nodes) {
            progress.step('.');
            addSize(nodes.parent(), missStats);
            log.log(Level.Debug, "=== FAILURE ===");
            logTrace(nodes, Integer.MAX_VALUE);
            log.log(Level.Debug, "===============");
        }

        private void addSize(SearchNode<?> node, DescriptiveStatistics stats) {
            if(node == null) {
                return;
            }
            SearchState s = node.output();
            s.state().unifier().size(project(VAR, s)).ifFinite(size -> {
                stats.addValue(size.doubleValue());
            });
        }

        private void logTrace(SearchElement node, int maxDepth) {
            if(node instanceof SearchNodes) {
                SearchNodes<?> nodes = (SearchNodes<?>) node;
                log.trace(" * {}", nodes.desc());
                logTrace(nodes.parent(), maxDepth);
            } else {
                SearchNode<?> traceNode = (SearchNode<?>) node;
                int depth = 0;
                do {
                    log.trace(" * [{}] {}", traceNode.id(), traceNode.desc());
                    if((depth++ == 0 || depth <= maxDepth) && traceNode.output() != null) {
                        SearchState state = traceNode.output();
                        state.print(ln -> log.trace("   {}", ln), (t, u) -> u.toString(t));
                    }
                } while((traceNode = traceNode.parent()) != null);
                log.trace(" # depth {}", depth);
            }
        }


        private void logStatsInfo(String name, DescriptiveStatistics stats) {
            log.info("{} {} of sizes {} (max/P80/P60/P40/P20/min)", name, stats.getN(), summary(stats));
        }

        private String summary(DescriptiveStatistics stats) {
            return String.format("%.1f/%.1f/%.1f/%.1f/%.1f/%.1f", stats.getMax(), stats.getPercentile(80),
                    stats.getPercentile(60), stats.getPercentile(40), stats.getPercentile(20), stats.getMin());
        }
    }

}