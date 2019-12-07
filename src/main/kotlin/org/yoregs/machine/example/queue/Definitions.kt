package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.*

interface QueueServerView<A> {

    fun on(
        queue: Key<ExternalChoice<QueueCommand>>
    ): QueueServerView<A> {
        TODO()
    }

    fun match(
        initializer: QueueServerView<A>.() -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Enq,
        initializer: QueueServerView<A>.(queue: Key<Lollipop<A>>) -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Deq,
        initializer: QueueServerView<A>.(queue: Key<InternalChoice<QueueEvent>>) -> Unit
    ) {
    }

    fun from(
        queue: Key<Lollipop<A>>
    ): QueueServerView<A> {
        TODO()
    }

    fun receive(
        initializer: QueueServerView<A>.(y: A, queue: Key<ExternalChoice<QueueCommand>>) -> Unit
    ): A {
        TODO()
    }

    fun tell(
        queue: Key<InternalChoice<QueueEvent>>
    ): QueueServerView<A> {
        TODO()
    }

    fun dot(
        case: QueueEvent.None,
        initializer: QueueServerView<A>.(queue: Key<Unit>) -> Unit
    ) {
    }

    fun dot(
        case: QueueEvent.Some,
        initializer: QueueServerView<A>.(queue: Key<Tensor<A>>) -> Unit
    ) {
    }

    fun close(
        queue: Key<Unit>
    ) {
    }

    fun impl(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) {
    }

    fun impl(
        queue: Key<ExternalChoice<QueueCommand>>,
        queueElemQueueDef: QueueElementDef<A>,
        y: A
    ) {
    }
}

interface QueueClientView<A> {

    fun tell(
        queue: Key<InternalChoice<QueueCommand>>
    ): QueueClientView<A> {
        TODO()
    }

    fun dot(
        case: QueueCommand.Enq,
        initializer: QueueClientView<A>.(queue: Key<Tensor<A>>) -> Unit
    ) {
    }

    fun by(
        queue: Key<Tensor<A>>
    ): QueueClientView<A> {
        TODO()
    }

    fun send(
        x: A,
        initializer: QueueClientView<A>.(queue: Key<InternalChoice<QueueCommand>>) -> Unit
    ) {
    }

    fun impl(
        queue: Key<InternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>
    ) {
    }

    fun again(
        queue: Key<InternalChoice<QueueCommand>>
    ) {
    }
}

interface QueueElementSig<A> : QueueServerView<A>, QueueClientView<A> {
    val initializer: QueueElementSig<A>.(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
}

class QueueElementDef<A>(
    override val initializer: QueueElementSig<A>.(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
) : QueueElementSig<A>

interface EmptyQueueSig<A> : QueueServerView<A> {
    val initializer: EmptyQueueSig<A>.(
        queue: Key<ExternalChoice<QueueCommand>>
    ) -> Unit
}

class EmptyQueueDef<A>(
    override val initializer: EmptyQueueSig<A>.(
        queue: Key<ExternalChoice<QueueCommand>>
    ) -> Unit
) : EmptyQueueSig<A>

interface QueueClientSig<A> : QueueClientView<A> {
    val initializer: QueueClientSig<A>.(
        client: Key<InternalChoice<QueueCommand>>
    ) -> Unit
}

class QueueClientDef<A>(
    override val initializer: QueueClientSig<A>.(
        client: Key<InternalChoice<QueueCommand>>
    ) -> Unit
) : QueueClientSig<A>
