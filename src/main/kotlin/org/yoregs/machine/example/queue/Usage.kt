package org.yoregs.machine.example.queue

//import org.yoregs.machine.builder.scenario
import org.yoregs.machine.builder.scenario
import org.yoregs.machine.builder.viewpoint
import org.yoregs.machine.example.queue.QueueCommand.Deq
import org.yoregs.machine.example.queue.QueueCommand.Enq
import org.yoregs.machine.example.queue.QueueEvent.None
import org.yoregs.machine.example.queue.QueueEvent.Some

val QueueServerViewpoint =
    viewpoint<QueueCommand, QueueEvent> {
        external(QueueCommand::class) {
            case(Deq) {
                internal(QueueEvent::class) {
                    dot(None) {
                        close()
                    }
                    dot(Some) {
                        tensor(String::class) {
                            external(QueueCommand::class) { }
                        }
                    }
                }
            }
            case(Enq) {
                lolly(String::class) {
                    external(QueueCommand::class) { }
                }
            }
        }
    }

val QueueClientViewpoint =
    viewpoint<QueueEvent, QueueCommand> {
        internal(QueueCommand::class) {
            dot(Deq) {
                external(QueueEvent::class) {
                    case(None) {
                        await()
                    }
                    case(Some) {
                        lolly(String::class) {
                            internal(QueueCommand::class) { }
                        }
                    }
                }
            }
            dot(Enq) {
                tensor(String::class) {
                    internal(QueueCommand::class) { }
                }
            }
        }
    }

val queueServerScenario =
    scenario<QueueScenarioBuilder> {
        // TODO: с чего начинается очередь?
        val queue = variable(QueueServerViewpoint)
//        val tail = variable(QueueClientViewpoint)
        match(queue) {
            case(Enq) {
                val elem: String = receive(queue)
                // TODO: отипобезопасить
                //  - никаких гарантий, что это корректное обращение к tail!
                //  - можно ли это зафорсить статически?
                dot(tail, Enq) {
                    send(tail, elem)
                    again(queue)
                }
            }
            case(Deq) {
                dot(queue, Some) {
                    send(queue, "Hello")
                    again(queue)
                }
            }
        }
    }

val queueClientScenario =
    scenario<QueueScenarioBuilder, QueueEvent, QueueCommand> {
        // TODO: смущает название переменной, т.к. по идее у клиента ссылка на очередь должна быть
        val client = variable(QueueClientViewpoint)
        dot(client, Enq) {
            send(client, "Hello")
            again(client)
        }
        dot(client, Enq) {
            send(client, "World!")
            again(client)
        }
    }
