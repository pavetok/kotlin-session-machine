package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.LinearVariable
import org.yoregs.machine.domain.Variable

fun <With : Choice, Plus : Choice> scenario(
    initializer: ScenarioBuilder<With, Plus>.() -> Unit
): ScenarioBuilder<With, Plus> {
    val processBuilder = ScenarioBuilder<With, Plus>()
    initializer.invoke(processBuilder)
    return processBuilder
}

open class ScenarioBuilder<With : Choice, Plus : Choice> {

    fun match(
        variable: Variable,
        initializer: ScenarioBuilder<With, Plus>.() -> Unit
    ): ScenarioBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun case(
        case: With,
        initializer: ScenarioBuilder<With, Plus>.() -> Unit
    ): ScenarioBuilder<With, Plus> {
        initializer.invoke(this)
        return this
    }

    inline fun <reified T> receive(variable: Variable): T {
        // TODO: прямо в билдере и возвращать?
        return T::class.java.newInstance()
    }

    fun <T> send(variable: Variable, value: T) {
    }

    fun dot(
        variable: Variable,
        case: Choice,
        initializer: ScenarioBuilder<With, Plus>.() -> Unit
    ): ScenarioBuilder<With, Plus> {
        return this.apply(initializer)
    }

    fun <A : Choice, B : Choice> variable(
        viewpoint: ViewpointBuilder<A, B>
    ): Variable {
        return LinearVariable()
    }

    fun again(variable: Variable) {
    }
}