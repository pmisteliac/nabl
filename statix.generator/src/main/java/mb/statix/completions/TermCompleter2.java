package mb.statix.completions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import mb.statix.constraints.CUser;
import mb.statix.generator.SearchState;
import mb.statix.generator.search.SStrategy;
import mb.statix.generator.search.StrategyContext;
import mb.statix.generator.search.StrategyNode;
import mb.statix.generator.search.StrategySearchState;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;
import mb.statix.spec.Spec;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;

import java.util.stream.Stream;

import static mb.statix.generator.search.Strategies.*;


/**
 * Finds completions.
 */
public final class TermCompleter2 {

    /** The Statix specification. */
    private final Spec spec;
    /** The constraint to solve. */
    private final IConstraint constraint;
    /** The search strategy to use. */
    private final SStrategy strategy;
    /** The logger. */
    private static final ILogger log = LoggerUtils.logger(TermCompleter2.class);

    public TermCompleter2(Spec spec, IConstraint constraint) {
        this.spec = spec;
        this.constraint = constraint;
        this.strategy = getStrategy();
    }

    public StrategyNode apply() {
        StrategySearchState initalState = StrategySearchState.of(SearchState.of(spec, State.of(spec), ImmutableList.of(constraint)));

        final StrategyContext ctx = new StrategyContext() {
        	
        	private final SetMultimap<String, Rule> unorderedRules = RuleUtil.makeUnordered(spec.rules());

			@Override
			public Spec getSpec() {
				return spec;
			}

			@Override
			public SetMultimap<String, Rule> getUnorderedRules() {
				return unorderedRules;
			}

        };
        return strategy.apply(ctx, StrategyNode.of(Stream.of(initalState)));
    }


    public SStrategy getStrategy() {
        return seq(
        	debug("start ->"),
            infer(),
            debug("-> infer1 ->"),
            delayStuckQueries(),
            debug("-> delayStuckQueries1 ->"),
            focus(c -> c instanceof CUser),
            debug("-> focus ->"),
            limit(1),
            debug("-> limit(1) ->"),
            expandRule(),
            debug("-> expandRule ->"),
            infer(),
            debug("-> infer2 ->"),
            delayStuckQueries(),
            debug("-> delayStuckQueries2 ->")
//            repeat(seq(
//                focus(c -> c instanceof CResolveQuery),
//                expandQuery(),
//                infer(),
//                delayStuckQueries()
//            ))
        );
    }

}
