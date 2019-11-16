package org.yoregs.machine.d

import org.yoregs.machine.d.QueueCommand.Deq
import org.yoregs.machine.d.QueueCommand.Enq
import org.yoregs.machine.d.QueueEvent.None
import org.yoregs.machine.d.QueueEvent.Some
import org.yoregs.machine.d.QueueSessionRole.Queue

sealed class QueueCommand {
    object Enq : QueueCommand()
    object Deq : QueueCommand()
}

sealed class QueueEvent {
    object None : QueueEvent()
    object Some : QueueEvent()
}

class SessionTypeBuilder {
    fun <T> with(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }

    fun <T> plus(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }

    fun <T> lolly(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }

    fun <T> tensor(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }

    fun then(continuation: SessionTypeBuilder): SessionTypeBuilder {
        return continuation
    }

    fun close(): SessionTypeBuilder {
        return this
    }

    fun wait(): SessionTypeBuilder {
        return this
    }

    fun label(case: Any, initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }
}

fun session(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
    return SessionTypeBuilder().apply(initializer)
}

val queueType = session {
    with<QueueCommand> {
        label(Deq) {
            plus<QueueEvent> {
                label(None) {
                    close()
                }
                label(Some) {
                    tensor<String> {
                        then(this)
                    }
                }
            }
        }
        label(Enq) {
            lolly<String> {
                then(this)
            }
        }
    }
}

val clientType = session {
    plus<QueueCommand> {
        label(Deq::class) {
            with<QueueEvent> {
                label(None) {
                    wait()
                }
                label(Some) {
                    lolly<String> {
                        then(this)
                    }
                }
            }
        }
        label(Enq::class) {
            tensor<String> {
                then(this)
            }
        }
    }
}

class SessionProcessBuilder<
        With,
        Plus> {

    var endpoint: ExternalChoice<With> = ExternalChoice<With>()

    fun match(
        endpoint: ExternalChoice<With>,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        this.endpoint = endpoint
        return this.apply(initializer)
    }

    fun case(
        case: With,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        initializer.invoke(this)
        return this
    }

    inline fun <reified P2> receive(endpoint: Lolly<P2>): P2 {
        return P2::class.java.newInstance()
    }

    fun <P1> send(endpoint: Tensor<P1>, value: P1): SessionProcessBuilder<With, Plus> {
        return this
    }

    fun dot(
        endpoint: InternalChoice<Plus>,
        case: Plus,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <R : Role, E : Endpoint> endpoint(role: R): E {
        return this.endpoint as E
    }

    fun forward(endpoint: ExternalChoice<With>): SessionProcessBuilder<With, Plus> {
        return this
    }
}

fun <T, R> process(type: Any, initializer: SessionProcessBuilder<T, R>.() -> Unit): SessionProcessBuilder<T, R> {
    val sessionProcessBuilder = SessionProcessBuilder<T, R>()
    initializer.invoke(sessionProcessBuilder)
    return sessionProcessBuilder
}

val queueProcess = process<QueueCommand, QueueEvent>(queueType) {
    val q1: ExternalChoice<QueueCommand> = endpoint(Queue)
    match(q1) {
        case(Enq) {
            val q2: Lolly<String> = endpoint(Queue)
            val elem = receive(q2)
            forward(q1)
        }
        case(Deq) {
            val q2: InternalChoice<QueueEvent> = endpoint(Queue)
            dot(q2, Some) {
                val q3: Tensor<String> = endpoint(Queue)
                send(q3, "Hello")
                forward(q1)
            }
        }
    }
}

interface Endpoint
interface Role

sealed class QueueSessionRole : Role {
    object Queue : QueueSessionRole()
}

class InternalChoice<T> : Endpoint
class ExternalChoice<T> : Endpoint
class Lolly<T> : Endpoint
class Tensor<T> : Endpoint
