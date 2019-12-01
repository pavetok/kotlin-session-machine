package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

fun scenario(
    initializer: ScenarioBuilder.() -> Unit
): ScenarioBuilder {
    val scenarioBuilder = ScenarioBuilder()
    initializer.invoke(scenarioBuilder)
    return scenarioBuilder
}

/**
 * Keep in mind:
 *  1. class level generics don't scale to dozens of type parameters
 *  2. self type pattern doesn't help with abstract types https://www.sitepoint.com/self-types-with-javas-generics
 */
@ScenarioMaker
class ScenarioBuilder {

    private lateinit var serverExternalChoice: ExternalChoiceBuilder<*>
    private lateinit var serverInternalChoice: InternalChoiceBuilder<*>
    private lateinit var clientExternalChoice: ExternalChoiceBuilder<*>
    private lateinit var clientInternalChoice: InternalChoiceBuilder<*>
    private val endpoints: MutableMap<Key<*>, Any> = mutableMapOf()

    fun <With : Choice> server(
        builder: ExternalChoiceBuilder<With>
    ): Key<ExternalChoiceBuilder<With>> {
        val key = Key<ExternalChoiceBuilder<With>>()
        endpoints[key] = builder
        return key
    }

    fun <Plus : Choice> server(
        builder: InternalChoiceBuilder<Plus>
    ): Variable {
        serverInternalChoice = builder
        return LinearVariable()
    }

    fun <Plus : Choice> client(
        builder: ExternalChoiceBuilder<Plus>
    ): Variable {
        clientExternalChoice = builder
        return LinearVariable()
    }

    fun <With : Choice> client(
        builder: InternalChoiceBuilder<With>
    ): Key<InternalChoiceBuilder<With>> {
        val key = Key<InternalChoiceBuilder<With>>()
        endpoints[key] = builder
        return key
    }

    fun <With : Choice> at(
        variable: Key<ExternalChoiceBuilder<With>>
    ): MatchBuilder<With> {
        return MatchBuilder(variable.cast(endpoints[variable]))
    }

    fun <With : Choice> to(
        variable: Variable
    ): DotBuilder<With> {
        @Suppress("UNCHECKED_CAST")
        return DotBuilder(clientInternalChoice as InternalChoiceBuilder<With>)
    }

    fun <With : Choice> to(
        variable: Key<InternalChoiceBuilder<With>>
    ): DotBuilder<With> {
        return DotBuilder(variable.cast(endpoints[variable]))
    }
}