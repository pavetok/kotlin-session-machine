package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import kotlin.reflect.KClass

fun <With : Choice, Plus : Choice> externalChoice(
    initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
): ExternalChoiceBuilder<With, Plus> {
    return ExternalChoiceBuilder<With, Plus>().apply(initializer)
}

open class ExternalChoiceBuilder<With : Choice, Plus : Choice> {

    lateinit var externalChoiceType: KClass<With>

    fun external(
        choiceType: KClass<With>,
        initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        externalChoiceType = choiceType
        return this.apply(initializer)
    }

    fun internal(
        choiceType: KClass<Plus>,
        initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> lolly(
        valueType: KClass<T>,
        initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> tensor(
        valueType: KClass<T>,
        initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun case(
        case: With, initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun dot(
        case: Plus, initializer: ExternalChoiceBuilder<With, Plus>.() -> Unit
    ): ExternalChoiceBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun close() {
    }

    fun await() {
    }
}