package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.*

sealed class QueueCommand : Choice {
    object Enq : QueueCommand()
    object Deq : QueueCommand()
    companion object
}

sealed class QueueEvent : Choice {
    object None : QueueEvent()
    object Some : QueueEvent()
}

class QueueExternalChoice : ExternalChoice<QueueCommand>
class QueueInternalChoice : InternalChoice<QueueEvent>
class QueueLolly : Lollipop<String>
class QueueTensor : Tensor<String>

class ClientInternalChoice : InternalChoice<QueueCommand>
class ClientExternalChoice : ExternalChoice<QueueEvent>
class ClientTensor : Tensor<String>
class ClientLolly : Lollipop<String>