package mb.statix.concurrent.p_raffrayi.impl.tokens;

import java.util.List;

import org.immutables.value.Value;

import mb.statix.concurrent.actors.IActorRef;
import mb.statix.concurrent.actors.futures.ICompletableFuture;
import mb.statix.concurrent.p_raffrayi.impl.IUnit;

@Value.Immutable(prehash = true)
public abstract class ATypeCheckerState<S, L, D> implements IWaitFor<S, L, D> {

    @Override @Value.Parameter public abstract IActorRef<? extends IUnit<S, L, D, ?>> origin();

    @Value.Parameter public abstract List<D> datums();

    @Value.Parameter public abstract ICompletableFuture<?> future();

    @Override public void visit(Cases<S, L, D> cases) {
        cases.on((TypeCheckerState<S, L, D>) this);
    }

    /*
     * CAREFUL
     * For this class, hashCode and equals are simplified to reference equality for performance.
     * This is possible because we never create a new instance which is used in isWaitingFor.
     * The tokens CloseScope & CloseLabel are created for such checks, and must have structural equality.
     */

    @Override public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override public boolean equals(Object obj) {
        return this == obj;
    }

}