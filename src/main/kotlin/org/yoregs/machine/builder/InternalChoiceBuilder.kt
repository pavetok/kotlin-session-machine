package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.ViewpointBuilder
import kotlin.reflect.KClass

fun <Plus : Choice> internal(
    choiceType: KClass<Plus>,
    initializer: InternalChoiceBuilder<Plus>.() -> Unit
): InternalChoiceBuilder<Plus> {
    return InternalChoiceBuilder<Plus>(choiceType).apply(initializer)
}

@ScenarioMaker
open class InternalChoiceBuilder<Plus : Choice>(
    private val choiceType: KClass<Plus>
) : ViewpointBuilder() {

    fun <With : Choice> external(
        choiceType: KClass<With>,
        initializer: ExternalChoiceBuilder<With>.() -> Unit
    ) {
    }

    fun <V : Any> tensor(
        valueType: KClass<V>,
        initializer: InternalChoiceBuilder<Plus>.() -> Unit
    ) {
    }

    fun dot(
        case: Plus,
        initializer: InternalChoiceBuilder<Plus>.() -> Unit
    ) {
    }

    fun close() {
    }
}