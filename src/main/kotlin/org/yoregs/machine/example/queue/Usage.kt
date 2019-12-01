package org.yoregs.machine.example.queue

import org.yoregs.machine.builder.external
import org.yoregs.machine.builder.internal
import org.yoregs.machine.builder.scenario
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

val queueServerScenario =
    scenario {
        val queue1 = server(QueueServerViewpoint)
        val tail1 = client(QueueClientViewpoint)
        at(queue1).match {
            case<String>(Enq) { queue2 ->
                val elem = from(queue2).receive()
                at(tail1).dot<String>(Enq) { tail2 ->
                    to(tail2).send(elem) {
                        again(queue1)
                    }
                }
            }
            esac<QueueEvent>(Deq) { queue2 ->
                at(queue2).dot<String>(Some) { queue3 ->
                    to(queue3).send("hello") {
                        again(queue1)
                    }
                }
            }
        }
    }

val queueClientScenario =
    scenario {
        val client1 = client(QueueClientViewpoint)
        to(client1).dot<String>(Enq) { client2 ->
            to(client2).send("hello") {
                again(client1)
            }
        }
        to(client1).dot<String>(Enq) { client2 ->
            to(client2).send("world") {
                again(client1)
            }
        }
    }
