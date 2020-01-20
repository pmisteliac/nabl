package mb.statix.generator.search;


import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import org.metaborg.util.functions.Function0;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Succeeds only if the number of states equals 1.
 */
public final class SingleStrategy implements SStrategy {

    /**
     * Initializes a new instance of the {@link SingleStrategy} class.
     */
    public SingleStrategy() { }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        List<StrategySearchState> collected = input.getStates().limit(2).collect(Collectors.toList());
        if (collected.size() != 1) {
            return StrategyNode.fail();
        }
        return StrategyNode.of(Stream.of(collected.get(0)));
    }

}
