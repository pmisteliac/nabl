package mb.statix.codecompletion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.constraints.CUser;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchState;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Rule;
import mb.statix.spec.Spec;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;

import javax.annotation.Nullable;
import java.util.*;

import static mb.statix.search.strategies.SearchStrategies.infer;


/**
 * Tests whether code completion can complete a program with a hole,
 * for which the full AST is known.
 */
public final class CompleteTest {

    private static final ILogger log = LoggerUtils.logger(CompleteTest.class);
    private final TermFactory termFactory = new TermFactory();

    /**
     *
     * @param spec
     * @param ruleName
     * @param completeAst
     * @param incompleteAst an incomplete AST with placeholders
     * @param completer
     * @throws InterruptedException
     */
    private void assertCanComplete(Spec spec, String ruleName, IStrategoTerm completeAst, IStrategoTerm incompleteAst, TermCompleter completer) throws InterruptedException {
        // Preparation: get the search state of the incomplete program
        SearchContext ctx = new SearchContext(spec);
        @Nullable SearchState state = analyze(spec, "", incompleteAst, ruleName, ctx);

        IStrategoTerm currentAst = incompleteAst;
        while (!currentAst.match(completeAst)) {
            List<CompletionProposal> proposals = completer.complete(ctx, state, "e");
            List<CompletionProposal> matchingProposals = findMatchingProposals(proposals, completeAst);
            if (matchingProposals.isEmpty()) {
                // No matching proposals
                throw new RuntimeException("No proposals.");
            } else if (matchingProposals.size() == 1) {
                // One matching proposal
                // TODO: apply it
                currentAst = applyCompletionProposal(currentAst, matchingProposals.get(0));
            } else {
                // More than one matching proposal
                // Find the most specific one and apply it?
                throw new RuntimeException("Not implemented.");
            }
        }
    }

//    private void pickCompletion

    /**
     * Finds the completion proposals that match the given AST.
     *
     * @param proposals the list of proposals
     * @param completeAst the complete AST to match against
     * @return the matching completion proposals; or an empty list when none are found
     */
    private List<CompletionProposal> findMatchingProposals(List<CompletionProposal> proposals, IStrategoTerm completeAst) {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Applies a completion proposal to the given incomplete AST.
     *
     * @param incompleteAst the incomplete AST
     * @param proposal the proposal to apply
     * @return the resulting (possibly incomplete) AST with the proposal applied
     */
    private IStrategoTerm applyCompletionProposal(IStrategoTerm incompleteAst, CompletionProposal proposal) {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Performs analysis on the given AST.
     *
     * @param spec the Statix specification
     * @param resourceName the name of the resource from which the AST was parsed
     * @param ast the AST to analyze
     * @param ruleName the name of the initial rule used to perform the analysis
     * @param ctx the search context
     * @return the state of the analyzed AST; or {@code null} when analysis failed
     */
    @Nullable
    private SearchState analyze(Spec spec, String resourceName, IStrategoTerm ast, String ruleName, SearchContext ctx) throws InterruptedException {
        StrategoTerms strategoTerms = new StrategoTerms(termFactory);
        String qualifiedName = mkQualifiedName("", ruleName);   // FIXME: Should Spec have a getName() property?
        IStrategoTerm annotatedAst = indexAst(resourceName, ast, termFactory);
        ITerm statixAst = strategoTerms.fromStratego(annotatedAst);

        if (!checkNoOverlappingRules(spec)) {
            // Analysis failed
            return null;
        }

        // TODO? <stx--explode> statixAst
        IConstraint constraint = new CUser(qualifiedName, Collections.singletonList(statixAst), null);

        SearchState state = SearchState.of(spec, State.of(spec), ImmutableList.of(constraint));
        Optional<SearchState> optResultState = infer().apply(ctx, state).findFirst();
        return optResultState.orElse(/* Analysis failed. */ null);

    }

    /**
     * Returns the qualified name of the rule.
     *
     * @param specName the name of the specification
     * @param ruleName the name of the rule
     * @return the qualified name of the rule, in the form of {@code <specName>!<ruleName>}.
     */
    private String mkQualifiedName(String specName, String ruleName) {
        if (specName.equals("") || ruleName.contains("!")) return ruleName;
        return specName + "!" + ruleName;
    }

    /**
     * Annotates the terms of the AST with term indices.
     *
     * @param resourceName the name of the resource from which the AST was parsed
     * @param ast the AST
     * @param termFactory the term factory to use to annotate the terms
     * @return the annotated AST
     */
    private IStrategoTerm indexAst(String resourceName, IStrategoTerm ast, TermFactory termFactory) {
        return StrategoTermIndices.index(ast, resourceName, termFactory);
    }

    /**
     * Reports any overlapping rules in the specification.
     *
     * @param spec the specification to check
     * @return {@code true} when the specification has no overlapping rules;
     * otherwise, {@code false}.
     */
    private static boolean checkNoOverlappingRules(Spec spec) {
        final ListMultimap<String, Rule> rulesWithEquivalentPatterns = spec.rules().getAllEquivalentRules();
        if(!rulesWithEquivalentPatterns.isEmpty()) {
            log.error("+--------------------------------------+");
            log.error("| FOUND RULES WITH EQUIVALENT PATTERNS |");
            log.error("+--------------------------------------+");
            for(Map.Entry<String, Collection<Rule>> entry : rulesWithEquivalentPatterns.asMap().entrySet()) {
                log.error("| Overlapping rules for: {}", entry.getKey());
                for(Rule rule : entry.getValue()) {
                    log.error("| * {}", rule);
                }
            }
            log.error("+--------------------------------------+");
        }
        return rulesWithEquivalentPatterns.isEmpty();
    }

}
