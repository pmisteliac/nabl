package mb.statix.concurrent.p_raffrayi.impl;

import java.util.Set;

import mb.statix.concurrent.actors.IActorRef;
import mb.statix.concurrent.actors.futures.IFuture;
import mb.statix.concurrent.p_raffrayi.nameresolution.DataLeq;
import mb.statix.concurrent.p_raffrayi.nameresolution.DataWf;
import mb.statix.concurrent.p_raffrayi.nameresolution.LabelOrder;
import mb.statix.concurrent.p_raffrayi.nameresolution.LabelWf;
import mb.statix.scopegraph.reference.EdgeOrData;
import mb.statix.scopegraph.reference.Env;
import mb.statix.scopegraph.terms.newPath.ScopePath;

/**
 * Protocol accepted by clients, from other clients
 */
public interface IUnit2UnitProtocol<S, L, D, R> {

    void _initShare(S scope, Iterable<EdgeOrData<L>> edges, boolean sharing);

    void _addShare(S scope);

    void _doneSharing(S scope);

    void _addEdge(S source, L label, S target);

    void _closeEdge(S scope, EdgeOrData<L> edge);

    IFuture<Env<S, L, D>> _query(ScopePath<S, L> path, LabelWf<L> labelWF, DataWf<S, L, D> dataWF,
            LabelOrder<L> labelOrder, DataLeq<S, L, D> dataEquiv);


    void _deadlockQuery(IActorRef<? extends IUnit<S, L, D, ?>> i, int m);

    void _deadlockReply(IActorRef<? extends IUnit<S, L, D, ?>> i, int m, Set<IActorRef<? extends IUnit<S, L, D, ?>>> r);

    void _deadlocked(Set<IActorRef<? extends IUnit<S, L, D, ?>>> nodes);

}