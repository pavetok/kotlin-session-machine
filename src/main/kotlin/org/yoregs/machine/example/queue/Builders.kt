package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.Key

interface QueueServerViewpoint<A> {

    fun on(
        queue: Key<QueueExternalChoice<QueueCommand>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun match(
        initializer: QueueServerViewpoint<A>.() -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Enq,
        initializer: QueueServerViewpoint<A>.(queue: Key<QueueLollipop<A>>) -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Deq,
        initializer: QueueServerViewpoint<A>.(queue: Key<QueueInternalChoice<QueueEvent>>) -> Unit
    ) {
    }

    fun from(
        queue: Key<QueueLollipop<A>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun receive(
        initializer: QueueServerViewpoint<A>.(y: A, queue: Key<QueueExternalChoice<QueueCommand>>) -> Unit
    ): A {
        TODO()
    }

    fun tell(
        queue: Key<QueueInternalChoice<QueueEvent>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun dot(
        case: QueueEvent.None,
        initializer: QueueServerViewpoint<A>.(queue: Key<QueueOne>) -> Unit
    ) {
    }

    fun dot(
        case: QueueEvent.Some,
        initializer: QueueServerViewpoint<A>.(queue: Key<QueueTensor<A>>) -> Unit
    ) {
    }

    fun close(
        queue: Key<QueueOne>
    ) {
    }

    fun again(
        queue: Key<QueueExternalChoice<QueueCommand>>,
        tail: Key<QueueInternalChoice<QueueCommand>>,
        x: A
    ) {
    }
}

interface QueueClientViewpoint<A> {

    fun tell(
        queue: Key<QueueInternalChoice<QueueCommand>>
    ): QueueClientViewpoint<A> {
        TODO()
    }

    fun dot(
        case: QueueCommand.Enq,
        initializer: QueueClientViewpoint<A>.(queue: Key<QueueTensor<A>>) -> Unit
    ) {
    }

    fun by(
        queue: Key<QueueTensor<A>>
    ): QueueClientViewpoint<A> {
        TODO()
    }

    fun send(
        x: A,
        initializer: QueueClientViewpoint<A>.(queue: Key<QueueInternalChoice<QueueCommand>>) -> Unit
    ) {
    }

    fun fwd(
        queue: Key<QueueInternalChoice<QueueCommand>>,
        tail: Key<QueueInternalChoice<QueueCommand>>
    ) {
    }

    fun again(
        queue: Key<QueueInternalChoice<QueueCommand>>
    ) {
    }
}

interface ElemSignature<A> : QueueServerViewpoint<A>, QueueClientViewpoint<A> {
    val initializer: ElemSignature<A>.(
        queue: Key<QueueExternalChoice<QueueCommand>>,
        tail: Key<QueueInternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
}

class ElemScenario<A>(
    override val initializer: ElemSignature<A>.(
        queue: Key<QueueExternalChoice<QueueCommand>>,
        tail: Key<QueueInternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
) : ElemSignature<A>

interface EmptySignature<A> : QueueServerViewpoint<A> {
    val initializer: EmptySignature<A>.(
        queue: Key<QueueExternalChoice<QueueCommand>>
    ) -> Unit
}

class EmptyScenario<A>(
    override val initializer: EmptySignature<A>.(
        queue: Key<QueueExternalChoice<QueueCommand>>
    ) -> Unit
) : EmptySignature<A>

interface ClientSignature<A> : QueueClientViewpoint<A> {
    val initializer: ClientSignature<A>.(
        client: Key<QueueInternalChoice<QueueCommand>>
    ) -> Unit
}

class ClientScenario<A>(
    override val initializer: ClientSignature<A>.(client: Key<QueueInternalChoice<QueueCommand>>) -> Unit
) : ClientSignature<A>
