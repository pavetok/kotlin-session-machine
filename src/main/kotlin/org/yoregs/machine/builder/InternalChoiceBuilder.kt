package org.yoregs.machine.builder

import org.yoregs.machine.domain.*
import kotlin.reflect.KClass

fun <Plus, A, With, B, T, C, D> internal(
    typeClass: KClass<A>,
    initializer: InternalChoiceBuilder<Plus, A, With, B, T, C, D>.() -> Unit
): InternalChoiceBuilder<Plus, A, With, B, T, C, D>
        where Plus : Choice,
              A : InternalChoice<Plus>,
              With : Choice,
              B : ExternalChoice<With>,
              T : Any,
              C : Tensor<T>,
              D : Lollipop<T> {
    return InternalChoiceBuilder<Plus, A, With, B, T, C, D>(typeClass).apply(initializer)
}

class InternalChoiceBuilder<Plus, A, With, B, T, C, D>(typeClass: KClass<A>)
        where Plus : Choice,
              A : InternalChoice<Plus>,
              With : Choice,
              B : ExternalChoice<With>,
              T : Any,
              C : Tensor<T>,
              D : Lollipop<T> {

    val typeClass: KClass<A> = typeClass
    lateinit var externalChoiceBuilder: ExternalChoiceBuilder<With, B, Plus, A, T, D, C>
    lateinit var tensorBuilder: TensorBuilder<T, C>

    fun dot(
        case: Plus,
        initializer: InternalChoiceBuilder<Plus, A, With, B, T, C, D>.() -> Unit
    ): InternalChoiceBuilder<Plus, A, With, B, T, C, D> {
        return this.apply(initializer)
    }

    fun external(
        typeClass: KClass<B>,
        initializer: ExternalChoiceBuilder<With, B, Plus, A, T, D, C>.() -> Unit
    ): ExternalChoiceBuilder<With, B, Plus, A, T, D, C> {
        externalChoiceBuilder = ExternalChoiceBuilder(typeClass)
        return externalChoiceBuilder.apply(initializer)
    }

    fun tensor(
        typeClass: KClass<C>,
        initializer: TensorBuilder<T, C>.() -> Unit
    ): TensorBuilder<T, C> {
        tensorBuilder = TensorBuilder(typeClass)
        return tensorBuilder.apply(initializer)
    }

    fun close() {

    }
}