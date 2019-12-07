package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.*

interface QueueServerViewpoint<A> {

    fun on(
        queue: Key<ExternalChoice<QueueCommand>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun match(
        initializer: QueueServerViewpoint<A>.() -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Enq,
        initializer: QueueServerViewpoint<A>.(queue: Key<Lollipop<A>>) -> Unit
    ) {
    }

    fun case(
        case: QueueCommand.Deq,
        initializer: QueueServerViewpoint<A>.(queue: Key<InternalChoice<QueueEvent>>) -> Unit
    ) {
    }

    fun from(
        queue: Key<Lollipop<A>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun receive(
        initializer: QueueServerViewpoint<A>.(y: A, queue: Key<ExternalChoice<QueueCommand>>) -> Unit
    ): A {
        TODO()
    }

    fun tell(
        queue: Key<InternalChoice<QueueEvent>>
    ): QueueServerViewpoint<A> {
        TODO()
    }

    fun dot(
        case: QueueEvent.None,
        initializer: QueueServerViewpoint<A>.(queue: Key<Unit>) -> Unit
    ) {
    }

    fun dot(
        case: QueueEvent.Some,
        initializer: QueueServerViewpoint<A>.(queue: Key<Tensor<A>>) -> Unit
    ) {
    }

    fun close(
        queue: Key<Unit>
    ) {
    }

    fun again(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) {
    }
}

interface QueueClientViewpoint<A> {

    fun tell(
        queue: Key<InternalChoice<QueueCommand>>
    ): QueueClientViewpoint<A> {
        TODO()
    }

    fun dot(
        case: QueueCommand.Enq,
        initializer: QueueClientViewpoint<A>.(queue: Key<Tensor<A>>) -> Unit
    ) {
    }

    fun by(
        queue: Key<Tensor<A>>
    ): QueueClientViewpoint<A> {
        TODO()
    }

    fun send(
        x: A,
        initializer: QueueClientViewpoint<A>.(queue: Key<InternalChoice<QueueCommand>>) -> Unit
    ) {
    }

    fun fwd(
        queue: Key<InternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>
    ) {
    }

    fun again(
        queue: Key<InternalChoice<QueueCommand>>
    ) {
    }
}

interface ElemSignature<A> : QueueServerViewpoint<A>, QueueClientViewpoint<A> {
    val initializer: ElemSignature<A>.(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
}

class ElemScenario<A>(
    override val initializer: ElemSignature<A>.(
        queue: Key<ExternalChoice<QueueCommand>>,
        tail: Key<InternalChoice<QueueCommand>>,
        x: A
    ) -> Unit
) : ElemSignature<A>

interface EmptySignature<A> : QueueServerViewpoint<A> {
    val initializer: EmptySignature<A>.(
        queue: Key<ExternalChoice<QueueCommand>>
    ) -> Unit
}

class EmptyScenario<A>(
    override val initializer: EmptySignature<A>.(
        queue: Key<ExternalChoice<QueueCommand>>
    ) -> Unit
) : EmptySignature<A>

interface ClientSignature<A> : QueueClientViewpoint<A> {
    val initializer: ClientSignature<A>.(
        client: Key<InternalChoice<QueueCommand>>
    ) -> Unit
}

class ClientScenario<A>(
    override val initializer: ClientSignature<A>.(
        client: Key<InternalChoice<QueueCommand>>
    ) -> Unit
) : ClientSignature<A>
