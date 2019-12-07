package org.yoregs.machine.example.queue

import org.yoregs.machine.builder.external
import org.yoregs.machine.builder.internal
import org.yoregs.machine.example.queue.QueueCommand.Deq
import org.yoregs.machine.example.queue.QueueCommand.Enq
import org.yoregs.machine.example.queue.QueueEvent.None
import org.yoregs.machine.example.queue.QueueEvent.Some

val QueueServerViewpoint =
    external(QueueCommand::class) {
        case(Deq) {
            internal(QueueEvent::class) {
                dot(None) {
                    close()
                }
                dot(Some) {
                    tensor(String::class) {
                        external(QueueCommand::class) {}
                    }
                }
            }
        }
        case(Enq) {
            lolly(String::class) {
                external(QueueCommand::class) {}
            }
        }

    }

val QueueClientViewpoint =
    internal(QueueCommand::class) {
        dot(Deq) {
            external(QueueEvent::class) {
                case(None) {
                    await()
                }
                case(Some) {
                    lolly(String::class) {
                        internal(QueueCommand::class) {}
                    }
                }
            }
        }
        dot(Enq) {
            tensor(String::class) {
                internal(QueueCommand::class) {}
            }
        }
    }

val elemQueueScenario = ElemScenario<String> { queue, tail, x ->
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
            tell(queue).dot(Some) { queue ->
                by(queue).send(x) { queue ->
                    impl(queue, tail)
                }
            }
        }
    }
}

val emptyQueueScenario = EmptyScenario<String> { queue ->
    on(queue).match {
        case(Enq) { queue ->
            from(queue).receive { y, queue ->
                impl(queue, elemQueueScenario, y)
            }
        }
        case(Deq) { queue ->
            tell(queue).dot(None) { queue ->
                close(queue)
            }

        }
    }
}

val clientQueueScenario = ClientScenario<String> { client ->
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
