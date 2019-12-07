package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.*
import org.yoregs.machine.example.queue.QueueCommand.Deq
import org.yoregs.machine.example.queue.QueueCommand.Enq
import org.yoregs.machine.example.queue.QueueEvent.None

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
        case: Enq,
        initializer: QueueServerView<A>.(queue: Key<Lollipop<A>>) -> Unit
    ) {
    }

    fun case(
        case: Deq,
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
        case: None,
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
        case: Enq,
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
    fun def(
        initializer: QueueServerView<A>.(
            queue: Key<ExternalChoice<QueueCommand>>,
            tail: Key<InternalChoice<QueueCommand>>,
            x: A
        ) -> Unit
    )
}

class QueueElementDef<A> : QueueElementSig<A> {

    init {
        def { queue, tail, x ->
            on(queue).match {
                case(Enq) { queue ->
                    from(queue).receive { y, queue ->
                        tell(tail).dot(Enq) { tail ->
                            by(tail).send(y) { tail ->
                                impl(queue, tail, x)
                            }
                        }
                    }
                }
                case(Deq) { queue ->
                    tell(queue).dot(QueueEvent.Some) { queue ->
                        by(queue).send(x) { queue ->
                            impl(queue, tail)
                        }
                    }
                }
            }

        }
    }

    override fun def(
        initializer: QueueServerView<A>.(
            queue: Key<ExternalChoice<QueueCommand>>,
            tail: Key<InternalChoice<QueueCommand>>,
            x: A
        ) -> Unit
    ) {
    }
}

interface EmptyQueueSig<A> : QueueServerView<A> {
    fun def(
        initializer: QueueServerView<A>.(
            queue: Key<ExternalChoice<QueueCommand>>
        ) -> Unit
    )
}

class EmptyQueueDef<A>(
    elementDef: QueueElementDef<A>
) : EmptyQueueSig<A> {

    init {
        def { queue ->
            on(queue).match {
                case(Enq) { queue ->
                    from(queue).receive { y, queue ->
                        impl(queue, elementDef, y)
                    }
                }
                case(Deq) { queue ->
                    tell(queue).dot(None) { queue ->
                        close(queue)
                    }
                }
            }
        }
    }

    override fun def(
        initializer: QueueServerView<A>.(
            queue: Key<ExternalChoice<QueueCommand>>
        ) -> Unit
    ) {
    }
}

interface QueueClientSig<A> : QueueClientView<A> {
    fun def(
        initializer: QueueClientSig<A>.(
            queue: Key<InternalChoice<QueueCommand>>
        ) -> Unit
    )
}

class HelloWorldClientDef : QueueClientSig<String> {

    init {
        def { client ->
            tell(client).dot(Enq) { client ->
                by(client).send("hello") { client ->
                    again(client)
                }
            }
            tell(client).dot(Enq) { client ->
                by(client).send("world") { client ->
                    again(client)
                }
            }
        }
    }

    override fun def(
        initializer: QueueClientSig<String>.(
            queue: Key<InternalChoice<QueueCommand>>
        ) -> Unit
    ) {
    }
}
