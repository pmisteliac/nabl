package org.metaborg.meta.nabl2.solver;

import static org.metaborg.meta.nabl2.util.Unit.unit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.metaborg.meta.nabl2.constraints.equality.CEqual;
import org.metaborg.meta.nabl2.constraints.equality.CInequal;
import org.metaborg.meta.nabl2.constraints.equality.IEqualityConstraint;
import org.metaborg.meta.nabl2.constraints.equality.IEqualityConstraint.CheckedCases;
import org.metaborg.meta.nabl2.constraints.equality.ImmutableCEqual;
import org.metaborg.meta.nabl2.constraints.messages.IMessageInfo;
import org.metaborg.meta.nabl2.constraints.messages.MessageContent;
import org.metaborg.meta.nabl2.terms.ITerm;
import org.metaborg.meta.nabl2.terms.ITermVar;
import org.metaborg.meta.nabl2.unification.UnificationException;
import org.metaborg.meta.nabl2.unification.Unifier;
import org.metaborg.meta.nabl2.util.Unit;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EqualitySolver extends AbstractSolverComponent<IEqualityConstraint> {

    private final Unifier unifier;

    private final Set<CInequal> defered;

    public EqualitySolver(Unifier unifier) {
        this.unifier = unifier;
        this.defered = Sets.newHashSet();
    }

    @Override public Class<IEqualityConstraint> getConstraintClass() {
        return IEqualityConstraint.class;
    }

    // ------------------------------------------------------------------------------------------------------//

    @Override public Unit add(IEqualityConstraint constraint) throws UnsatisfiableException {
        return constraint.matchOrThrow(CheckedCases.of(this::add, this::add));
    }

    @Override public boolean iterate() throws UnsatisfiableException, InterruptedException {
        return doIterate(defered, this::solve);
    }

    @Override public Iterable<CInequal> finish() {
        return defered;
    }

    // ------------------------------------------------------------------------------------------------------//

    private Unit add(CEqual constraint) throws UnsatisfiableException {
        solve(constraint);
        return unit;
    }

    private Unit add(CInequal constraint) throws UnsatisfiableException {
        if(!solve(constraint)) {
            defered.add(constraint);
        }
        return unit;
    }

    // ------------------------------------------------------------------------------------------------------//

    private boolean solve(CEqual constraint) throws UnsatisfiableException {
        ITerm left = unifier.find(constraint.getLeft());
        ITerm right = unifier.find(constraint.getRight());
        try {
            unifier.unify(left, right);
        } catch(UnificationException ex) {
            MessageContent content =
                MessageContent.builder().append("Cannot unify ").append(left).append(" with ").append(right).build();
            throw new UnsatisfiableException(constraint.getMessageInfo().withDefault(content));
        }
        return true;
    }

    private boolean solve(CInequal constraint) throws UnsatisfiableException {
        ITerm left = unifier.find(constraint.getLeft());
        ITerm right = unifier.find(constraint.getRight());
        if(left.equals(right)) {
            MessageContent content = MessageContent.builder().append(constraint.getLeft().toString()).append(" and ")
                .append(constraint.getRight().toString()).append(" must be inequal, but both resolve to ")
                .append(constraint.getLeft()).build();
            throw new UnsatisfiableException(constraint.getMessageInfo().withDefault(content));
        }
        return !unifier.canUnify(left, right);
    }

    // ------------------------------------------------------------------------------------------------------//

    @Override public Collection<IEqualityConstraint> getNormalizedConstraints(IMessageInfo messageInfo) {
        List<IEqualityConstraint> constraints = Lists.newArrayList();
        for(ITermVar var : unifier.getActiveVars()) {
            constraints.add(ImmutableCEqual.of(var, unifier.find(var), messageInfo));
        }
        return constraints;
    }

}