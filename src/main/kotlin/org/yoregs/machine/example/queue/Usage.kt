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
        // TODO: с чего начинается очередь?
        val client = server(QueueServerViewpoint)
        val tail = client(QueueClientViewpoint)
        at<QueueCommand>(client).match {
            case(Enq) {
                // TODO: как-то избавиться от параметров
                val elem = from<String>(client).receive(String::class)
                to<QueueCommand>(tail).dot(Enq) {
                    to<String>(tail).send(elem) {
                        again(client)
                    }
                }
            }
            case(Deq) {
                to<QueueEvent>(client).dot(Some) {
                    to<String>(client).send("hello") {
                        again(client)
                    }
                }
            }
        }
    }

val queueClientScenario =
    scenario {
        val queue = client(QueueClientViewpoint)
        to<QueueCommand>(queue).dot(Enq) {
            to<String>(queue).send("hello") {
                again(queue)
            }
        }
        to<QueueCommand>(queue).dot(Enq) {
            to<String>(queue).send("world") {
                again(queue)
            }
        }
    }
