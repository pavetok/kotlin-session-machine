package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

inline fun <reified B> scenario(
    initializer: B.() -> Unit
): B {
    val scenarioBuilder = B::class.java.newInstance()
    initializer.invoke(scenarioBuilder)
    return scenarioBuilder
}

abstract class ScenarioBuilder<B, With : Choice, Plus : Choice> {

    abstract val self: B

    fun match(
        variable: Variable,
        initializer: B.() -> Unit
    ): B {
        initializer.invoke(self)
        return self
    }

    fun case(
        case: With,
        initializer: B.() -> Unit
    ): B {
        initializer.invoke(self)
        return self
    }

    inline fun <reified T> receive(variable: Variable): T {
        // TODO: прямо в билдере и возвращать?
        return T::class.java.newInstance()
    }

    fun <T> send(variable: Variable, value: T) {
    }

    fun <T> send(endpoint: Tensor<T>, value: T) {
    }

    fun dot(
        variable: Variable,
        case: Choice,
        initializer: B.() -> Unit
    ): B {
        initializer.invoke(self)
        return self
    }

    fun dot(
        endpoint: InternalChoice<Plus>,
        case: Choice,
        initializer: B.() -> Unit
    ): B {
        initializer.invoke(self)
        return self
    }

    fun <A : Choice, B : Choice> variable(
        viewpoint: ViewpointBuilder<A, B>
    ): Variable {
        return LinearVariable()
    }

    fun again(variable: Variable) {
    }
}