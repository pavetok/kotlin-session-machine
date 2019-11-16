package org.yoregs.machine.d

import org.yoregs.machine.d.QueueCommand.Deq
import org.yoregs.machine.d.QueueCommand.Enq
import org.yoregs.machine.d.QueueEvent.None
import org.yoregs.machine.d.QueueEvent.Some
import org.yoregs.machine.d.QueueRole.Client
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

    fun external(
        choiceType: KClass<With>,
        initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun internal(
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

    fun case(case: With, initializer: SessionTypeBuilder<With, Plus>.() -> Unit): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun tell(case: Plus, initializer: SessionTypeBuilder<With, Plus>.() -> Unit): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun close(): SessionTypeBuilder<With, Plus> {
        return this
    }

    fun wait(): SessionTypeBuilder<With, Plus> {
        return this
    }
}

fun <With : Choice, Plus : Choice> session(
    initializer: SessionTypeBuilder<With, Plus>.() -> Unit
): SessionTypeBuilder<With, Plus> {
    return SessionTypeBuilder<With, Plus>().apply(initializer)
}

class SessionProcessBuilder<With : Choice, Plus : Choice>(sessionType: SessionTypeBuilder<With, Plus>) {

    var sessionType: SessionTypeBuilder<With, Plus> = sessionType
    var endpoint = ExternalChoice<With>()

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

    inline fun <reified T> receive(endpoint: Lolly<T>): T {
        // TODO: прямо в билдере и возвращать?
        return T::class.java.newInstance()
    }

    fun <T> send(endpoint: Tensor<T>, value: T): SessionProcessBuilder<With, Plus> {
        return this
    }

    fun tell(
        endpoint: InternalChoice<Plus>,
        case: Plus,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <R : Role, E : Endpoint> endpoint(role: R): E {
        // TODO: обезопасить
        return this.endpoint as E
    }

    fun forward(endpoint: Endpoint): SessionProcessBuilder<With, Plus> {
        return this
    }
}

fun <With : Choice, Plus : Choice> process(
    sessionType: SessionTypeBuilder<With, Plus>,
    initializer: SessionProcessBuilder<With, Plus>.() -> Unit
): SessionProcessBuilder<With, Plus> {
    val sessionProcessBuilder = SessionProcessBuilder<With, Plus>(sessionType)
    initializer.invoke(sessionProcessBuilder)
    return sessionProcessBuilder
}

// CLIENT CODE

sealed class QueueRole : Role {
    object Queue : QueueRole()
    object Client : QueueRole()
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
    external(QueueCommand::class) {
        case(Deq) {
            internal(QueueEvent::class) {
                tell(None) {
                    close()
                }
                tell(Some) {
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

val clientType = session<QueueEvent, QueueCommand> {
    internal(QueueCommand::class) {
        tell(Deq) {
            external(QueueEvent::class) {
                case(None) {
                    wait()
                }
                case(Some) {
                    lolly(String::class) {
                        internal(QueueCommand::class) { }
                    }
                }
            }
        }
        tell(Enq) {
            tensor(String::class) {
                internal(QueueCommand::class) { }
            }
        }
    }
}

val queueProcess = process(queueType) {
    // TODO: безопасное внедрение эндпоинтов
    val q1: ExternalChoice<QueueCommand> = endpoint(Queue)
    match(q1) {
        case(Enq) {
            val q2: Lolly<String> = endpoint(Queue)
            val elem = receive(q2)
            forward(q1)
        }
        case(Deq) {
            val q2: InternalChoice<QueueEvent> = endpoint(Queue)
            tell(q2, Some) {
                val q3: Tensor<String> = endpoint(Queue)
                send(q3, "Hello")
                forward(q1)
            }
        }
    }
}

val clientProcess = process(clientType) {
    val c1: InternalChoice<QueueCommand> = endpoint(Client)
    tell(c1, Enq) {
        val c2: Tensor<String> = endpoint(Client)
        send(c2, "Hello")
        forward(c1)
    }
    // TODO: добавление N элементов
}
