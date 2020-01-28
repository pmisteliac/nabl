package mb.statix.search;

import mb.statix.spec.Spec;


/**
 * The context in which the search is performed.
 */
public final class StrategyContext {

    private final Spec spec;

    /**
     * Initializes a new instance of the {@link StrategyContext} class.
     * @param spec the specification
     */
    public StrategyContext(Spec spec) {
        this.spec = spec;
    }

    /** Gets the specification. */
    public Spec getSpec() {
        return spec;
    }

}
