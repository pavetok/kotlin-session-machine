package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import kotlin.reflect.KClass

fun <With : Choice> external(
    choiceType: KClass<With>,
    initializer: ExternalChoiceBuilder<With>.() -> Unit
): ExternalChoiceBuilder<With> {
    return ExternalChoiceBuilder<With>(choiceType).apply(initializer)
}

@ScenarioMaker
open class ExternalChoiceBuilder<With : Choice>(choiceType: KClass<With>) {

    val choiceType = choiceType

    fun <Plus : Choice> internal(
        choiceType: KClass<Plus>,
        initializer: InternalChoiceBuilder<Plus>.() -> Unit
    ) {
    }

    fun <T : Any> lolly(
        valueType: KClass<T>,
        initializer: ExternalChoiceBuilder<With>.() -> Unit
    ): ExternalChoiceBuilder<With> {
        return this.apply(initializer)
    }

    fun case(
        case: With, initializer: ExternalChoiceBuilder<With>.() -> Unit
    ): ExternalChoiceBuilder<With> {
        return this.apply(initializer)
    }

    fun await() {
    }
}