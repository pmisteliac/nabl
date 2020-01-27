package mb.statix.cli;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.util.TermFormatter;
import mb.statix.completions.TermCompleter;
import mb.statix.completions.TermCompleter2;
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
        final CSearchLogger searchLog = new CSearchLogger(this.STX, this.log, resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();

        final TermCompleter com = new TermCompleter(spec, statixGen.constraint(), new Paret(spec).complete(), searchLog);
        final Stream<SearchState> resultStream = com.apply().nodes().map(sn -> {
            searchLog.success(sn);
            return sn.output();
        });

        log.info("Completing...");
        final List<SearchState> results = Lists.newArrayList(resultStream.iterator());
        searchLog.progress.done();
        log.info("Completed to {} alternatives:", results.size());
        results.forEach(s -> {
            System.out.println(searchLog.pretty.apply(s));
        });
        log.info("Done.");
    }

    public void run2(String file) throws MetaborgException {
        log.info("Reading " + file + "...");
        final FileObject resource = this.spoofax.resolve(file);
        final CSearchLogger searchLog = new CSearchLogger(this.STX, this.log, resource);

        final StatixGenerator statixGen = new StatixGenerator(this.spoofax, this.context, resource);
        final Spec spec = statixGen.spec();
        final TermCompleter2 com = new TermCompleter2(spec, statixGen.constraint());

        log.info("Completing...");
        List<StrategySearchState> results = com.apply().getStates().collect(Collectors.toList());
        log.log(Level.Debug, "=== SUCCESS ===");
        for (StrategySearchState ss : results) {
            log.log(Level.Debug, " * {}", searchLog.pretty.apply(ss));
        }
        log.log(Level.Debug, "---- Trace ----");
        results.forEach(ss -> {
            ss.print(ln -> log.trace("   {}", ln), (t, u) -> u.toString(t));
        });
        log.log(Level.Debug, "===============");

        log.info("Completed to {} alternatives:", results.size());
        results.forEach(s -> {
            System.out.println(searchLog.pretty.apply(s));
        });
        log.info("Done.");
    }









}