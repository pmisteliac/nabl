package mb.statix.solver.query;

import static mb.nabl2.terms.build.TermBuild.B;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.matching.MatchException;
import mb.nabl2.terms.unification.CannotUnifyException;
import mb.nabl2.util.Tuple2;
import mb.statix.scopegraph.reference.LabelWF;
import mb.statix.scopegraph.reference.ResolutionException;
import mb.statix.solver.Completeness;
import mb.statix.solver.Delay;
import mb.statix.solver.Solver;
import mb.statix.solver.State;
import mb.statix.solver.log.IDebugContext;
import mb.statix.spec.Lambda;

public class ConstraintLabelWF implements LabelWF<ITerm> {

    private final Lambda constraint;
    private final State state;
    private final Completeness completeness;
    private final IDebugContext debug;

    private final List<ITerm> labels;

    public ConstraintLabelWF(Lambda constraint, State state, Completeness completeness, IDebugContext debug) {
        this(constraint, state, completeness, debug, ImmutableList.of());
    }

    private ConstraintLabelWF(Lambda constraint, State state, Completeness completeness, IDebugContext debug,
            Iterable<ITerm> labels) {
        this.constraint = constraint;
        this.state = state;
        this.completeness = completeness;
        this.debug = debug;
        this.labels = ImmutableList.copyOf(labels);
    }

    public LabelWF<ITerm> step(ITerm l) {
        final List<ITerm> labels = ImmutableList.<ITerm>builder().addAll(this.labels).add(l).build();
        return new ConstraintLabelWF(constraint, state, completeness, debug, labels);
    }

    @Override public boolean wf() throws ResolutionException, InterruptedException {
        final ITerm term = B.newList(labels);
        debug.info("Check {} well-formed", state.unifier().toString(term));
        try {
            final Tuple2<State, Lambda> result = constraint.apply(ImmutableList.of(term), state);
            try {
                if(Solver.entails(result._1(), result._2().body(), completeness, result._2().bodyVars(),
                        debug.subContext()).isPresent()) {
                    debug.info("Well-formed {}", state.unifier().toString(term));
                    return true;
                } else {
                    debug.info("Not well-formed {}", state.unifier().toString(term));
                    return false;
                }
            } catch(Delay d) {
                throw new ResolutionDelayException("Label well-formedness delayed.", d);
            }
        } catch(MatchException | CannotUnifyException ex) {
            return false;
        }
    }

    @Override public boolean empty() throws ResolutionException, InterruptedException {
        final Tuple2<ITermVar, State> varAndState = state.freshVar("lbls");
        final ITermVar var = varAndState._1();
        final ITerm term = B.newListTail(labels, var);
        debug.info("Check {} empty", state.unifier().toString(term));
        try {
            final Tuple2<State, Lambda> result = constraint.apply(ImmutableList.of(term), varAndState._2());
            try {
                final Set<ITermVar> localVars =
                        ImmutableSet.<ITermVar>builder().addAll(result._2().bodyVars()).add(var).build();
                if(Solver.entails(result._1(), result._2().body(), completeness, localVars, debug.subContext())
                        .isPresent()) {
                    debug.info("Non-empty {}", state.unifier().toString(term));
                    return false;
                } else {
                    debug.info("Empty {}", state.unifier().toString(term));
                    return true;
                }
            } catch(Delay d) {
                // If we are stuck on the tail variable, it means we are not empty.
                // This is regardless of whether we are also stuck on other context
                // variables. Otherwise we require more context and delay.
                if(d.vars().contains(var)) {
                    return false;
                } else {
                    throw new ResolutionDelayException("Label well-formedness delayed.", d); // WAS: false?
                }
            }
        } catch(MatchException | CannotUnifyException ex) {
            return false;
        }
    }

}