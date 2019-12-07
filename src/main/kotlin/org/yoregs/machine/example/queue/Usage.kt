package org.yoregs.machine.example.queue

import org.yoregs.machine.example.queue.QueueCommand.Deq
import org.yoregs.machine.example.queue.QueueCommand.Enq
import org.yoregs.machine.example.queue.QueueEvent.None
import org.yoregs.machine.example.queue.QueueEvent.Some

val queueElementDef = QueueElementDef<String> { queue, tail, x ->
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

val emptyQueueDef = EmptyQueueDef<String> { queue ->
    on(queue).match {
        case(Enq) { queue ->
            from(queue).receive { y, queue ->
                impl(queue, queueElementDef, y)
            }
        }
        case(Deq) { queue ->
            tell(queue).dot(None) { queue ->
                close(queue)
            }

        }
    }
}

val queueClientDef = QueueClientDef<String> { client ->
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

// TODO:
//  1. Исполнение
//  2. Обобщение
