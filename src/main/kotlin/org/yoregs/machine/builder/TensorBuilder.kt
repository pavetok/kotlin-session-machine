package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.Key
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
open class TensorBuilder<V> where V : Any {

    fun send(
        value: V,
        initializer: TensorBuilder<V>.() -> Unit
    ) {
    }

    fun again(variable: Variable) {
    }

    fun <With : Choice> again(variable: Key<ExternalChoiceBuilder<With>>) {
    }
}