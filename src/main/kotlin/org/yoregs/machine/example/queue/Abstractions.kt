package org.yoregs.machine.example.queue

import org.yoregs.machine.domain.Choice

sealed class QueueCommand : Choice {
    object Enq : QueueCommand()
    object Deq : QueueCommand()
}

sealed class QueueEvent : Choice {
    object None : QueueEvent()
    object Some : QueueEvent()
}

class QueueExternalChoice<T>
class QueueInternalChoice<T>
class QueueLollipop<T>
class QueueOne
class QueueTensor<T>
