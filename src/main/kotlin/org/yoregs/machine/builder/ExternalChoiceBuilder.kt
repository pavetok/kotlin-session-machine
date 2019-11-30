package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.ViewpointBuilder
import kotlin.reflect.KClass

fun <With : Choice> external(
    choiceType: KClass<With>,
    initializer: ExternalChoiceBuilder<With>.() -> Unit
): ExternalChoiceBuilder<With> {
    return ExternalChoiceBuilder(choiceType).apply(initializer)
}

@ScenarioMaker
open class ExternalChoiceBuilder<With : Choice>(
    private val choiceType: KClass<With>
) : ViewpointBuilder() {
    lateinit var internalChoice: InternalChoiceBuilder<*>

    fun <Plus : Choice> internal(
        choiceType: KClass<Plus>,
        initializer: InternalChoiceBuilder<Plus>.() -> Unit
    ) {
        val builder = InternalChoiceBuilder(choiceType)
        internalChoice = builder
        initializer.invoke(builder)
    }

    fun <V : Any> lolly(
        valueType: KClass<V>,
        initializer: ExternalChoiceBuilder<With>.() -> Unit
    ) {
    }

    fun case(
        case: With,
        initializer: ExternalChoiceBuilder<With>.() -> Unit
    ) {
    }

    fun await() {
    }
}