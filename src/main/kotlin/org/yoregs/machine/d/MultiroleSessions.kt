package org.yoregs.machine.d

import org.yoregs.machine.d.ElementChoice.None
import org.yoregs.machine.d.ElementChoice.Some
import org.yoregs.machine.d.QueueChoice.Deq
import org.yoregs.machine.d.QueueChoice.Enq

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

    fun case(case: Any, initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
        return this.apply(initializer)
    }
}

fun session(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
    return SessionTypeBuilder().apply(initializer)
}

val queueType = session {
    with<QueueChoice> {
        case(Deq) {
            plus<ElementChoice> {
                case(None) {
                    close()
                }
                case(Some) {
                    tensor<String> {
                        then(this)
                    }
                }
            }
        }
        case(Enq) {
            lolly<String> {
                then(this)
            }
        }
    }
}

val clientType = session {
    plus<QueueChoice> {
        case(Deq) {
            with<ElementChoice> {
                case(None) {
                    wait()
                }
                case(Some) {
                    lolly<String> {
                        then(this)
                    }
                }
            }
        }
        case(Enq) {
            tensor<String> {
                then(this)
            }
        }
    }
}

class SessionProcessBuilder {
    fun caseL(case: Any, initializer: SessionProcessBuilder.() -> Unit): SessionProcessBuilder {
        return this.apply(initializer)
    }
}

fun process(type: Any, initializer: SessionProcessBuilder.() -> Unit): SessionProcessBuilder {
    return SessionProcessBuilder().apply(initializer)
}

val queueProcess = process(queueType) {
    caseL(Deq) {

    }
}