package mb.nabl2.terms.unification.u;

import java.util.Map.Entry;
import java.util.Optional;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.substitution.ISubstitution;
import mb.nabl2.terms.unification.OccursException;
import mb.nabl2.terms.unification.TermSize;

/**
 * Unification
 * 
 * The following should hold:
 * 
 * <code>
 *   if (d', U') = U.unify(_, _) then U.compose(d') == U'
 *   !U.remove(v).varSet().contains(v)
 *   if U.varSet().contains(v) then !U.remove(v).freeVarSet().contains(v)
 *   Sets.intersection(U.varSet(), U.freeVarSet()).isEmpty()
 *   if (d', U') = U.remove(v), and t' = d'.findRecursive(t) then U.findRecursive(t) == U'.findRecursive(t')
 *   if (d', U') = U.remove(v) then U'.compose(d') == U
 * </code>
 * 
 * Internal invariants:
 * 
 * <code>
 *   terms.values().noneMatch(t -> t instanceOf ITermVar)
 *   Sets.intersection(reps.keySet(), terms.keySet()).isEmpty()
 * </code>
 * 
 * Support for recursive terms is easy to add, but makes many operations exceptional. For example: remove(ITermVar),
 * findRecursive(ITerm).
 *
 *
 */


public interface IUnifier {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods on the unifier
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Check if the unifier is finite, or whether it allows recursive terms.
     */
    boolean isFinite();

    /**
     * Check if the substitution is empty.
     */
    boolean isEmpty();

    /**
     * Test if the unifier contains a substitution for the given variable.
     */
    boolean contains(ITermVar var);

    /**
     * Return the domain of this unifier.
     */
    Set.Immutable<ITermVar> varSet();

    /**
     * Return the set of free variables appearing in this unifier.
     */
    Set.Immutable<ITermVar> freeVarSet();

    /**
     * Test if the unifier contains any cycles.
     */
    boolean isCyclic();

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods on a single term
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find the representative variable for the given variable.
     */
    ITermVar findRep(ITermVar var);

    /**
     * Return of the variable (or it representative) has a term.
     */
    boolean hasTerm(ITermVar var);

    /**
     * Find the representative term for the given term. The representative itself is not instantiated, to prevent
     * exponential blowup in time or space. If the given term is a variable, the representative term is returned, or the
     * class variable if the variable is free in the unifier. If the given term is not a variable, it is returned
     * unchanged.
     */
    ITerm findTerm(ITerm term);

    /**
     * Fully instantiate the given term according to this substitution. Instantiation may result in exponential blowup
     * of the term size. This operation preserves term sharing as much as possible. This operation throws an exception
     * on recursive terms.
     */
    ITerm findRecursive(ITerm term);

    /**
     * Test if the given term is ground relative to this unifier.
     */
    boolean isGround(ITerm term);

    /**
     * Test if the given term is cyclic relative to this unifier.
     */
    boolean isCyclic(ITerm term);

    /**
     * Return the set of variables that appear in the given term relative to this unifier.
     */
    Set.Immutable<ITermVar> getVars(ITerm term);

    /**
     * Return the size of the given term relative to this unifier.
     */
    TermSize size(ITerm term);

    /**
     * Return a string representation of the given term.
     */
    String toString(ITerm term);

    /**
     * Return a string representation of the given term, up to a certain term depth.
     */
    String toString(ITerm term, int n);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods on a single term
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return a unifier that makes these terms equal, relative to the current unifier.
     * 
     * If no result is returned, the terms are unequal. Otherwise, if an empty unifier is returned, the terms are equal.
     * Finally, if a non-empty unifier is returned, the terms are not equal, but can be made equal by the returned
     * unifier.
     */
    Optional<? extends IUnifier.Immutable> diff(ITerm term1, ITerm term2);

    ///////////////////////////////////////////
    // asMap()
    ///////////////////////////////////////////

    Map.Immutable<ITermVar, ITerm> equalityMap();


    public interface Immutable extends IUnifier {

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Methods on two terms
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Unify the two input terms. Return an updated unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Result<? extends Immutable>> unify(ITerm term1, ITerm term2) throws OccursException;

        /**
         * Unify with the given unifier. Return an updated unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Result<? extends Immutable>> unify(IUnifier other) throws OccursException;

        /**
         * Unify the two term pairs. Return a diff unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Result<? extends Immutable>>
                unify(Iterable<? extends Entry<? extends ITerm, ? extends ITerm>> equalities) throws OccursException;

        /**
         * Return a substitution that only retains the given variable in the domain. Also returns a substitution to
         * eliminate the removed variables from terms.
         */
        Result<ISubstitution.Immutable> retain(ITermVar var);

        /**
         * Return a substitution that only retains the given variables in the domain. Also returns a substitution to
         * eliminate the removed variables from terms.
         */
        Result<ISubstitution.Immutable> retainAll(Iterable<ITermVar> vars);

        /**
         * Return a unifier with the given variable removed from the domain. Returns a substitution to eliminate the
         * variable from terms.
         */
        Result<ISubstitution.Immutable> remove(ITermVar var);

        /**
         * Return a unifier with the given variables removed from the domain. Returns a substitution to eliminate the
         * variable from terms.
         */
        Result<ISubstitution.Immutable> removeAll(Iterable<ITermVar> vars);

        /**
         * Return transient version of this unifier.
         */
        Transient melt();

    }

    /**
     * Interface that gives a result and an updated immutable unifier.
     */
    public interface Result<T> {

        T result();

        Immutable unifier();

    }

    public interface Transient extends IUnifier {

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Methods on two terms
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Unify the two input terms. Return a diff unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Immutable> unify(ITerm term1, ITerm term2) throws OccursException;

        /**
         * Unify with the given unifier. Return a diff unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Immutable> unify(IUnifier other) throws OccursException;

        /**
         * Unify the two term pairs. Return a diff unifier, or throw if the terms cannot be unified.
         */
        Optional<? extends Immutable> unify(Iterable<? extends Entry<? extends ITerm, ? extends ITerm>> equalities)
                throws OccursException;

        /**
         * Retain only the given variable in the domain of this unifier. Returns a substitution to eliminate the removed
         * variables from terms.
         */
        ISubstitution.Immutable retain(ITermVar var);

        /**
         * Retain only the given variables in the domain of this unifier. Returns a substitution to eliminate the
         * removed variables from terms.
         */
        ISubstitution.Immutable retainAll(Iterable<ITermVar> vars);

        /**
         * Remove the given variable from the domain of this unifier. Returns a substitution to eliminate the variable
         * from terms.
         */
        ISubstitution.Immutable remove(ITermVar var);

        /**
         * Remove the given variables from the domain of this unifier. Returns a substitution to eliminate the variable
         * from terms.
         */
        ISubstitution.Immutable removeAll(Iterable<ITermVar> vars);

        /**
         * Return immutable version of this unifier. The transient unifier cannot be used anymore after this call.
         */
        Immutable freeze();

    }

}