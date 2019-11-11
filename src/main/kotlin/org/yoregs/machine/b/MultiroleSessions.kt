package org.yoregs.machine.b

interface Role
// channel or session
interface Formula

interface Sender<out T> : Role {
    fun send(): T
}

interface Receiver<in T> : Role {
    fun receive(choice: T)
}

interface Choice : Formula {
}

interface InternalChoice<out T>
interface ExternalChoice<in T>

sealed class ClientChoice {
    data class Enq(val element: String) : ClientChoice()
    object Deq : ClientChoice()
}

class QueueExternalChoice {
    fun enq(): QueueLolly {
        return QueueLolly();
    }

    fun deq(): QueueInternalChoice {
        return QueueInternalChoice()
    }
}

class QueueLolly {
    fun receive(s: String): QueueExternalChoice {
        return QueueExternalChoice()
    }
}

class QueueInternalChoice {
    fun send(client: ClientExternalChoice): QueueTensor {
        return QueueTensor(client)
    }
}

sealed class QueueChoice {
    data class Some(val element: String) : QueueChoice()
    object None : QueueChoice()
}

class QueueTensor(val client: ClientExternalChoice) {
    fun send(): QueueExternalChoice {
        return QueueExternalChoice()
    }
}

class ClientExternalChoice {
    fun none() {
    }

    fun some(s: String) {
    }
}
