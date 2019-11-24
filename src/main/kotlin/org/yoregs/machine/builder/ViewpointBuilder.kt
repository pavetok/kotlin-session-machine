package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import kotlin.reflect.KClass

fun <With : Choice, Plus : Choice> viewpoint(
    initializer: ViewpointBuilder<With, Plus>.() -> Unit
): ViewpointBuilder<With, Plus> {
    return ViewpointBuilder<With, Plus>().apply(initializer)
}

open class ViewpointBuilder<With : Choice, Plus : Choice> {

    lateinit var externalChoiceType: KClass<With>

    fun external(
        choiceType: KClass<With>,
        initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        externalChoiceType = choiceType
        return this.apply(initializer)
    }

    fun internal(
        choiceType: KClass<Plus>,
        initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> lolly(
        valueType: KClass<T>,
        initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <T : Any> tensor(
        valueType: KClass<T>,
        initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun case(
        case: With, initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun dot(
        case: Plus, initializer: ViewpointBuilder<With, Plus>.() -> Unit
    ): ViewpointBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun close() {
    }

    fun await() {
    }
}