package org.yoregs.machine.a

interface Role
// channel or session
interface Formula

// endpoint? linear type on endpoint?
interface Interpretation<R, F> where R : Role, F : Formula

interface Sender<out T> : Role {
    fun send(): T
}

interface Receiver<in T> : Role {
    fun receive(choice: T)
}

interface Choice : Formula {
}

interface InternalChoice<T> : Sender<T>, Choice
interface ExternalChoice<T> : Receiver<T>, Choice

sealed class ClientChoice {
    data class Enq(val element: String) : ClientChoice()
    object Deq : ClientChoice()
}

class QueueExternalChoice : ExternalChoice<ClientChoice> {
    override fun receive(choice: ClientChoice) {
        return when (choice) {
            is ClientChoice.Enq -> Unit
            is ClientChoice.Deq -> Unit
        }
    }
}
