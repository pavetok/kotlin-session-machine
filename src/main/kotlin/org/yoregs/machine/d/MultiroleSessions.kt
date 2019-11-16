package org.yoregs.machine.d

import org.yoregs.machine.d.QueueCommand.Deq
import org.yoregs.machine.d.QueueCommand.Enq
import org.yoregs.machine.d.QueueEvent.None
import org.yoregs.machine.d.QueueEvent.Some
import org.yoregs.machine.d.QueueRole.Queue
import kotlin.reflect.KClass

// LIBRARY CODE

interface Endpoint
interface Role
interface Choice

class InternalChoice<T> : Endpoint
class ExternalChoice<T> : Endpoint
class Lolly<T> : Endpoint
class Tensor<T> : Endpoint

class SessionTypeBuilder<With : Choice, Plus : Choice> {

    fun externalChoice(
        choiceType: KClass<With>,
        initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun internalChoice(
        choiceType: KClass<Plus>,
        initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> lolly(
        valueType: KClass<T>,
        initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> tensor(
        valueType: KClass<T>,
        initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun close(): SessionTypeBuilder<With, Plus> {
        return this
    }

    fun wait(): SessionTypeBuilder<With, Plus> {
        return this
    }

    // TODO: можно ли конкретизировать Choice?
    fun case(case: Choice, initializer: SessionTypeBuilder<With, Plus>.() -> Unit): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }
}

fun <With : Choice, Plus : Choice> session(initializer: SessionTypeBuilder<With, Plus>.() -> Unit): SessionTypeBuilder<With, Plus> {
    return SessionTypeBuilder<With, Plus>().apply(initializer)
}

class SessionProcessBuilder<With : Choice, Plus : Choice> {  // TODO: можно ли без дженериков на уровне класса?

    var endpoint: ExternalChoice<With> = ExternalChoice()

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
        return this.endpoint as E  // TODO: type safety
    }

    fun forward(endpoint: ExternalChoice<With>): SessionProcessBuilder<With, Plus> {
        return this
    }
}

fun <T : Choice, R : Choice> process(
    typeBuilder: SessionTypeBuilder<T, R>,
    initializer: SessionProcessBuilder<T, R>.() -> Unit
): SessionProcessBuilder<T, R> {
    val sessionProcessBuilder = SessionProcessBuilder<T, R>()
    initializer.invoke(sessionProcessBuilder)
    return sessionProcessBuilder
}

// CLIENT CODE

sealed class QueueRole : Role {
    object Queue : QueueRole()
}

sealed class QueueCommand : Choice {
    object Enq : QueueCommand()
    object Deq : QueueCommand()
    companion object
}

sealed class QueueEvent : Choice {
    object None : QueueEvent()
    object Some : QueueEvent()
}

val queueType = session<QueueCommand, QueueEvent> {
    externalChoice(QueueCommand::class) {
        case(Deq) {
            internalChoice(QueueEvent::class) {
                case(None) {
                    close()
                }
                case(Some) {
                    tensor(String::class) {
                        externalChoice(QueueCommand::class) { }
                    }
                }
            }
        }
        case(Enq) {
            lolly(String::class) {
                externalChoice(QueueCommand::class) { }
            }
        }
    }
}

val clientType = session<QueueEvent, QueueCommand> {
    internalChoice(QueueCommand::class) {
        case(Deq) {
            externalChoice(QueueEvent::class) {
                case(None) {
                    wait()
                }
                case(Some) {
                    lolly(String::class) {
                        internalChoice(QueueCommand::class) { }
                    }
                }
            }
        }
        case(Enq) {
            tensor(String::class) {
                internalChoice(QueueCommand::class) { }
            }
        }
    }
}

val queueProcess = process(queueType) {
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
