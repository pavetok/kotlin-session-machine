package org.yoregs.machine.d

import org.yoregs.machine.d.QueueCommand.Deq
import org.yoregs.machine.d.QueueCommand.Enq
import org.yoregs.machine.d.QueueEvent.None
import org.yoregs.machine.d.QueueEvent.Some
import org.yoregs.machine.d.QueueVar.*
import kotlin.reflect.KClass

// LIBRARY CODE

interface Endpoint
interface Role
interface Choice
interface Var

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

    fun case(
        case: With, initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun dot(
        case: Plus, initializer: SessionTypeBuilder<With, Plus>.() -> Unit
    ): SessionTypeBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun close() {
    }

    fun await() {
    }
}

fun <With : Choice, Plus : Choice> session(
    initializer: SessionTypeBuilder<With, Plus>.() -> Unit
): SessionTypeBuilder<With, Plus> {
    return SessionTypeBuilder<With, Plus>().apply(initializer)
}

class SessionProcessBuilder<With : Choice, Plus : Choice> {

    fun match(
        endpoint: Var,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun case(
        case: With,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        initializer.invoke(this)
        return this
    }

    inline fun <reified T> receive(endpoint: Var): T {
        // TODO: прямо в билдере и возвращать?
        return T::class.java.newInstance()
    }

    fun <T> send(endpoint: Var, value: T) {
    }

    fun dot(
        endpoint: Var,
        case: Choice,
        initializer: SessionProcessBuilder<With, Plus>.() -> Unit
    ): SessionProcessBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun endpoint(name: Var, type: SessionTypeBuilder<With, Plus>) {
    }

    fun again(endpoint: Var) {
    }
}

fun <With : Choice, Plus : Choice> process(
    initializer: SessionProcessBuilder<With, Plus>.() -> Unit
): SessionProcessBuilder<With, Plus> {
    val sessionProcessBuilder = SessionProcessBuilder<With, Plus>()
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

sealed class QueueVar : Var {
    object queue : QueueVar()
    object tail : QueueVar()
    object client : QueueVar()
}

val QueueType = session<QueueCommand, QueueEvent> {
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

val ClientType = session<QueueEvent, QueueCommand> {
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

val queueProcess = process<QueueCommand, QueueEvent> {
    // TODO: типа обезопасить?
    endpoint(queue, QueueType)
    // TODO: с чего начинается очередь?
    endpoint(tail, QueueType)
    match(queue) {
        case(Enq) {
            val elem: String = receive(queue)
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

val clientProcess = process<QueueEvent, QueueCommand> {
    // TODO: смущает название переменной, т.к. по идее у клиента ссылка на очередь должна быть
    endpoint(client, ClientType)
    dot(client, Enq) {
        send(client, "Hello")
        again(client)
    }
    dot(client, Enq) {
        send(client, "World!")
        again(client)
    }
}
