package org.yoregs.machine.c

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
    data class Enq(val element: String) : QueueChoice()
    object Deq : QueueChoice()
}

typealias QueueProvider = ExternalChoice<QueueChoice>
typealias QueueClient = InternalChoice<QueueChoice>

class MyQueueProvider : QueueProvider {
    override fun receive(choice: QueueChoice) {
    }
}

class MyQueueClient : QueueClient {
    override fun send(): QueueChoice {
        return QueueChoice.Enq("foo");
    }
}

class SessionTypeBuilder {
    fun build(): String {
        TODO("implement")
    }

    fun with(choice: ExternalChoice<QueueChoice>) {
    }
}

fun session(initializer: SessionTypeBuilder.() -> Unit): SessionTypeBuilder {
    return SessionTypeBuilder().apply(initializer)
}

val s = session {
    with(MyQueueProvider())
}