package mb.nabl2.terms.matching;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

import java.util.Set;

import org.metaborg.util.functions.Action2;

import com.google.common.collect.ImmutableSet;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.substitution.ISubstitution.Transient;
import mb.nabl2.terms.unification.IUnifier.Immutable;

class NilPattern extends Pattern {
    private static final long serialVersionUID = 1L;

    public NilPattern() {
    }

    @Override public Set<ITermVar> getVars() {
        return ImmutableSet.of();
    }

    @Override protected boolean matchTerm(ITerm term, Transient subst, Immutable unifier, Eqs eqs) {
        // @formatter:off
        return M.list(listTerm -> {
            return listTerm.match(ListTerms.<Boolean>cases()
                .nil(nilTerm -> {
                    return true;
                }).var(v -> {
                    eqs.add(v, this);
                    return true;
                }).otherwise(t -> {
                    return false;
                })
            );
        }).match(unifier.findTerm(term)).orElse(false);
        // @formatter:on
    }

    @Override
    protected ITerm asTerm(Action2<ITermVar, ITerm> equalities) {
        return B.newNil();
    }

    @Override public String toString() {
        return "[]";
    }

}