package mb.statix.search;

import mb.statix.spec.Spec;


/**
 * The context in which the search is performed.
 */
public final class SearchContext {

    private final Spec spec;

    /**
     * Initializes a new instance of the {@link SearchContext} class.
     * @param spec the specification
     */
    public SearchContext(Spec spec) {
        this.spec = spec;
    }

    /** Gets the specification. */
    public Spec getSpec() {
        return spec;
    }

}
