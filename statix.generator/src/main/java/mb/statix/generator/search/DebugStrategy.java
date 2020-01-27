package mb.statix.generator.search;


import org.metaborg.util.log.ILogger;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Prints the states.
 */
public final class DebugStrategy implements SStrategy {

    private ILogger log;
    private String prefix;

    /**
     * Initializes a new instance of the {@link DebugStrategy} class.
     */
    public DebugStrategy(ILogger log, String prefix) {
        this.log = log;
        this.prefix = prefix;
    }

    @Override
    public StrategyNode apply(StrategyContext context, StrategyNode input) {
        if (prefix != null) log.info("=== " + prefix + " ===");
        else log.info("================");

        int prefixLength;
        if (prefix != null) prefixLength = 8 + prefix.length();
        else prefixLength = 16;

        // Force evaluation
        final List<StrategySearchState> states = input.getStates().collect(Collectors.toList());

        if (!states.isEmpty()) {
            log.info("{}", states.get(0));
            for (StrategySearchState s : states.subList(1, states.size())) {
                log.info(new String(new char[prefixLength]).replace("\0", "-"));
                log.info("{}", s);
            }
        }

        log.info(new String(new char[prefixLength]).replace("\0", "="));

        return StrategyNode.of(states.stream());
    }


    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public String toString(boolean inParens) {
        return "debug";
    }

}
