package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.ViewpointBuilder
import kotlin.reflect.KClass

fun <With : Choice> external(
    choiceType: KClass<With>,
    initializer: ExternalChoiceBuilder<With>.() -> Unit
): ExternalChoiceBuilder<With> {
    return ExternalChoiceBuilder<With>(choiceType).apply(initializer)
}

@ScenarioMaker
open class ExternalChoiceBuilder<With : Choice>(
    private val choiceType: KClass<With>
) {
    lateinit var internalChoice: ViewpointBuilder

    fun <Plus : Choice> internal(
        choiceType: KClass<Plus>,
        initializer: InternalChoiceBuilder<Plus>.() -> Unit
    ) {
        internalChoice = InternalChoiceBuilder(choiceType)
        initializer.invoke(internalChoice.cast())
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