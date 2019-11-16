package org.yoregs.machine.d

import org.yoregs.machine.d.ElementChoice.None
import org.yoregs.machine.d.ElementChoice.Some
import org.yoregs.machine.d.QueueChoice.Deq
import org.yoregs.machine.d.QueueChoice.Enq
import org.yoregs.machine.d.QueueSessionRole.Queue

interface Role
// channel or session
interface Formula

interface Client : Role {
}

interface Provider : Role {
}

interface Choice : Formula {
}

interface InternalChoice<out Choice> {
    fun send(): Choice
}

interface ExternalChoice<in Choice> {
    fun receive(choice: Choice)
}

sealed class QueueChoice {
    object Enq : QueueChoice()
    object Deq : QueueChoice()
}

sealed class ElementChoice {
    object None : ElementChoice()
    object Some : ElementChoice()
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
    with<QueueChoice> {
        label(Deq) {
            plus<ElementChoice> {
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
    plus<QueueChoice> {
        label(Deq::class) {
            with<ElementChoice> {
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

class SessionProcessBuilder {

    fun <T> match(chan: With<T>, initializer: SessionProcessBuilder.() -> Unit): SessionProcessBuilder {
        return this.apply(initializer)
    }

    fun case(case: Any, initializer: SessionProcessBuilder.() -> Unit): SessionProcessBuilder {
        initializer.invoke(this)
        return this
    }

    inline fun <reified T> receive(chan: Lolly<T>): T {
        return T::class.java.newInstance()
    }

    fun <T> send(chan: Tensor<T>, payload: T): SessionProcessBuilder {
        return this
    }

    fun <T> dot(chan: Plus<T>, case: T, initializer: SessionProcessBuilder.() -> Unit): SessionProcessBuilder {
        return this.apply(initializer)
    }

    inline fun <reified T> endpoint(name: QueueSessionRole): T {
        return T::class.java.newInstance()
    }

    fun <T> fwd(chan: With<T>): SessionProcessBuilder {
        return this
    }
}

fun process(type: Any, initializer: SessionProcessBuilder.(With<QueueChoice>) -> Unit): SessionProcessBuilder {
    val sessionProcessBuilder = SessionProcessBuilder()
    initializer.invoke(sessionProcessBuilder, With())
    return sessionProcessBuilder
}

val queueProcess = process(queueType) {
    val q1: With<QueueChoice> = endpoint(Queue)
    match(q1) {
        case(Enq) {
            val q2: Lolly<String> = endpoint(Queue)
            val elem = receive(q2)
            fwd(q1)
        }
        case(Deq) {
            val q2: Plus<ElementChoice> = endpoint(Queue)
            dot(q2, Some) {
                val q3: Tensor<String> = endpoint(Queue)
                send(q3, "Hello")
                fwd(q1)
            }
        }
    }
}

sealed class QueueSessionRole {
    object Queue : QueueSessionRole()
}

class Plus<T>
class With<T>
class Lolly<T>
class Tensor<T>
