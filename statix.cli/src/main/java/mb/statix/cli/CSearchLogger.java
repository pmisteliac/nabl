package mb.statix.cli;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.generator.SearchLogger;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchElement;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.search.SStrategy;
import mb.statix.generator.search.StrategyNode;
import mb.statix.generator.search.StrategySearchState;
import mb.statix.generator.util.StreamProgressPrinter;
import mb.statix.solver.IConstraint;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.spoofax.core.shell.StatixGenerator;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.Level;

import java.util.stream.Collectors;

import static mb.nabl2.terms.build.TermBuild.B;


public class CSearchLogger implements SearchLogger {

    private static final String VAR = "e";
    private final Statix STX;
    private final ILogger log;
    public final DescriptiveStatistics hitStats = new DescriptiveStatistics();
    public final DescriptiveStatistics missStats = new DescriptiveStatistics();
    public final Function1<SearchState, String> pretty;

    public final StreamProgressPrinter progress = new StreamProgressPrinter(System.err, 80, out -> {
        long hits = hitStats.getN();
        long all = hits + missStats.getN();
        out.println(" " + hits + "/" + all + " " + summary(hitStats));
    });

    public CSearchLogger(Statix STX, ILogger log, FileObject resource) {
        this.STX = STX;
        this.log = log;
        this.pretty = getSearchStatePrinter(resource);
    }

    @Override public void init(long seed, SearchStrategy<?, ?> strategy, Iterable<IConstraint> constraints) {
        log.info("seed {}", seed);
        log.info("strategy {}", strategy);
        log.info("constraints {}", constraints);
    }

    @Override public void init(long seed, SStrategy strategy, Iterable<IConstraint> constraints) {
        log.info("seed: {}", seed);
        log.info("strategy: {}", strategy.toString(true));
        log.info("constraints: {}", constraints);
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

    @Override
    public void success(StrategyNode n) {
        progress.step('+');
//        addSize(n, hitStats);
        log.log(Level.Debug, "=== SUCCESS ===");
        for (StrategySearchState ss : n.getStates().collect(Collectors.toList())) {
            log.log(Level.Debug, " * {}", pretty.apply(ss));
        }
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

    @Override
    public void failure(StrategyNode n) {
        progress.step('.');
//            addSize(nodes.parent(), missStats);
        log.log(Level.Debug, "=== FAILURE ===");
        logTrace(n, Integer.MAX_VALUE);
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

    private void logTrace(StrategyNode node, int maxDepth) {
        log.trace( " * {}", node);
        node.getStates().forEach(ss -> {
            ss.print(ln -> log.trace("   {}", ln), (t, u) -> u.toString(t));
        });
    }

    private void logStatsInfo(String name, DescriptiveStatistics stats) {
        log.info("{} {} of sizes {} (max/P80/P60/P40/P20/min)", name, stats.getN(), summary(stats));
    }

    private String summary(DescriptiveStatistics stats) {
        return String.format("%.1f/%.1f/%.1f/%.1f/%.1f/%.1f", stats.getMax(), stats.getPercentile(80),
                stats.getPercentile(60), stats.getPercentile(40), stats.getPercentile(20), stats.getMin());
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
}