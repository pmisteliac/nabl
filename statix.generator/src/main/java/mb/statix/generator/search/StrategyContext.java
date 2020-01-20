package mb.statix.generator.search;

import com.google.common.collect.SetMultimap;
import mb.statix.spec.Rule;
import mb.statix.spec.Spec;


/**
 * The context of the search strategies.
 */
public interface StrategyContext {

    /**
     * Gets the specification.
     *
     * @return the specification
     */
    Spec getSpec();

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
