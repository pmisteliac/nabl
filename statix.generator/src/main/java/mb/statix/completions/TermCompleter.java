package mb.statix.completions;

import com.google.common.collect.ImmutableList;

import mb.statix.generator.SearchContext;
import mb.statix.generator.SearchLogger;
import mb.statix.generator.SearchState;
import mb.statix.generator.SearchStrategy;
import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Finds completions.
 */
public final class TermCompleter {

    /** The Statix specification. */
    private final Spec spec;
    /** The constraint to solve. */
    private final IConstraint constraint;
    /** The search strategy to use. */
    private final SearchStrategy<SearchState, SearchState> strategy;
    /** The search logger to use. */
    private final SearchLogger log;

    public TermCompleter(Spec spec, IConstraint constraint, SearchStrategy<SearchState, SearchState> strategy, SearchLogger log) {
        this.spec = spec;
        this.constraint = constraint;
        this.strategy = strategy;
        this.log = log;
    }

    public SearchNodes<SearchState> apply() {
        SearchState initalState = SearchState.of(spec, State.of(spec), ImmutableList.of(constraint));

        final long seed = System.currentTimeMillis();
        log.init(seed, strategy, initalState.constraintsAndDelays());

        final AtomicInteger nodeId = new AtomicInteger();
        final Random rnd = new Random(seed);
        final SearchContext ctx = new SearchContext() {
        	
//        	private final SetMultimap<String, Rule> unorderedRules = RuleUtil.makeUnordered(spec.rules());

			@Override
			public Spec spec() {
				return spec;
			}

//			@Override
//			public SetMultimap<String, Rule> getUnorderedRules() {
//				return unorderedRules;
//			}

            @Override public Random rnd() {
                return rnd;
            }

            @Override public int nextNodeId() {
                return nodeId.incrementAndGet();
            }

            @Override public void failure(SearchNodes<?> nodes) {
                log.failure(nodes);
            }

        };
        SearchNode<SearchState> initNode = new SearchNode<>(ctx.nextNodeId(), initalState, null, "init");
        return strategy.apply(ctx, initNode);
    }

}
