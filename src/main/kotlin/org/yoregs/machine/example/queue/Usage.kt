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
        val queue = server(QueueServerViewpoint)
        val tail = client(QueueClientViewpoint)
        at(queue).match {
            case(Enq) {
                // TODO: как избавиться от параметров? как подсказать по-другому?
                val elem = from<String>(queue).receive(String::class)
                at(tail).dot(Enq) {
                    to<String>(tail).send(elem) {
                        again(queue)
                    }
                }
            }
            case(Deq) {
                at<QueueEvent>(queue).dot(Some) {
                    to<String>(queue).send("hello") {
                        again(queue)
                    }
                }
            }
        }
    }

val queueClientScenario =
    scenario {
        val client = client(QueueClientViewpoint)
        to<QueueCommand>(client).dot(Enq) {
            to<String>(client).send("hello") {
                again(client)
            }
        }
        to<QueueCommand>(client).dot(Enq) {
            to<String>(client).send("world") {
                again(client)
            }
        }
    }
