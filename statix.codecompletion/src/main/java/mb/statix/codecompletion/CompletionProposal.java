package mb.statix.codecompletion;

import mb.nabl2.terms.ITerm;


/**
 * A completion proposal.
 */
public final class CompletionProposal {

    private final ITerm term;

    /**
     * Initializes a new instance of the {@link CompletionProposal} class.
     *
     * @param term the proposed term
     */
    public CompletionProposal(ITerm term) {
        this.term = term;
    }

    /**
     * Gets the proposed term.
     *
     * @return the proposed term
     */
    public ITerm getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return term.toString();
    }

}
