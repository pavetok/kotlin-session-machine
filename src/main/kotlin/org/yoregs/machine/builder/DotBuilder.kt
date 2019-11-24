package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class DotBuilder<A>(
    private val internalChoice: InternalChoiceBuilder<A>
) where A : Choice {

    fun dot(
        case: A,
        initializer: DotBuilder<A>.() -> Unit
    ) {
    }

    fun <V : Any> to(
        variable: Variable
    ): TensorBuilder<V> {
        return TensorBuilder()
    }
}