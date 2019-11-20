package org.yoregs.machine.builder

import org.yoregs.machine.domain.*
import kotlin.reflect.KClass

fun <With, A, Plus, B, T, C, D> external(
    typeClass: KClass<A>,
    initializer: ExternalChoiceBuilder<With, A, Plus, B, T, C, D>.() -> Unit
): ExternalChoiceBuilder<With, A, Plus, B, T, C, D>
        where With : Choice,
              A : ExternalChoice<With>,
              Plus : Choice,
              B : InternalChoice<Plus>,
              T : Any,
              C : Lollipop<T>,
              D : Tensor<T> {
    return ExternalChoiceBuilder<With, A, Plus, B, T, C, D>(typeClass).apply(initializer)
}

class ExternalChoiceBuilder<With, A, Plus, B, T, C, D>(typeClass: KClass<A>)
        where With : Choice,
              A : ExternalChoice<With>,
              Plus : Choice,
              B : InternalChoice<Plus>,
              T : Any,
              C : Lollipop<T>,
              D : Tensor<T> {

    val typeClass: KClass<A> = typeClass
    lateinit var internalChoiceBuilder: InternalChoiceBuilder<Plus, B, With, A, T, D, C>
    lateinit var lollyBuilder: LollyBuilder<T, C>

    fun case(
        case: With,
        initializer: ExternalChoiceBuilder<With, A, Plus, B, T, C, D>.() -> Unit
    ): ExternalChoiceBuilder<With, A, Plus, B, T, C, D> {
        return this.apply(initializer)
    }

    fun internal(
        typeClass: KClass<B>,
        initializer: InternalChoiceBuilder<Plus, B, With, A, T, D, C>.() -> Unit
    ): InternalChoiceBuilder<Plus, B, With, A, T, D, C> {
        internalChoiceBuilder = InternalChoiceBuilder(typeClass)
        return internalChoiceBuilder.apply(initializer)
    }

    fun lolly(
        typeClass: KClass<C>,
        initializer: LollyBuilder<T, C>.() -> Unit
    ): LollyBuilder<T, C> {
        lollyBuilder = LollyBuilder(typeClass)
        return lollyBuilder.apply(initializer)
    }

    fun await() {

    }
}