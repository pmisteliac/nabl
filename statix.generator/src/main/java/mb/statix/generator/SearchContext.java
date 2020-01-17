package mb.statix.generator;

import java.util.Random;

import com.google.common.collect.SetMultimap;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.spec.Rule;
import mb.statix.spec.Spec;


/**
 * The search context.
 */
public interface SearchContext {

    /**
     * The specification.
     *
     * @return the specification
     */
    Spec spec();

    /**
     * The source of randomness.
     *
     * @return the source of randomness
     */
    Random rnd();

    /**
     * Provides the unique ID of the next search node.
     *
     * @return the next unique ID
     */
    int nextNodeId();


    void failure(SearchNodes<?> nodes);


    /**
     * Returns a set of rules where the match order is reflected in (dis)equality constraints in the rule bodies. The
     * resulting rules can be applied independent of the other rules in the set.
     * Note that compared to using applyAll, mismatches may only be discovered when the body of the returned rules is
     * evaluated, instead of during the matching process already.
     *
     * @return a set of rules that are mutually independent
     */
    SetMultimap<String, Rule> getUnorderedRules();

}