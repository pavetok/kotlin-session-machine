package org.yoregs.machine.example.queue

import org.yoregs.machine.builder.ScenarioBuilder
import org.yoregs.machine.builder.TensorBuilder
import org.yoregs.machine.domain.Tensor

class QueueScenarioBuilder : ScenarioBuilder<QueueScenarioBuilder, QueueCommand, QueueEvent>() {

    override val self: QueueScenarioBuilder
        get() = this
}

class QueueTensorBuilder : TensorBuilder() {
    var tail: Tensor<String>? = null
}
