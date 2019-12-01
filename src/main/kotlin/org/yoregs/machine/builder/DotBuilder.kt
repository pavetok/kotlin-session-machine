package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

@ScenarioMaker
class DotBuilder<A>(
    private val internalChoice: InternalChoiceBuilder<A>
) where A : Choice {

    fun <V : Any> dot(
        case: A,
        initializer: DotBuilder<A>.(key: Key<Tensor<V>>) -> Unit
    ) {
    }

    fun <V : Any> to(
        variable: Variable
    ): TensorBuilder<V> {
        return TensorBuilder()
    }

    fun <V : Any> to(
        variable: Key<Tensor<V>>
    ): TensorBuilder<V> {
        return TensorBuilder()
    }
}