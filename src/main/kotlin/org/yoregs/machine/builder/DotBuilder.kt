package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class DotBuilder<With> where With : Choice {

    fun <Case : With> dot(
        case: Case,
        initializer: DotBuilder<With>.() -> Unit
    ) {
    }

    fun <V : Any> to(
        variable: Variable
    ): TensorBuilder<V> {
        return TensorBuilder<V>()
    }
}