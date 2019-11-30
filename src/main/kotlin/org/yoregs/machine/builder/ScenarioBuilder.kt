package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.LinearVariable
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

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

    fun <With : Choice> server(
        builder: ExternalChoiceBuilder<With>
    ): Variable {
        serverExternalChoice = builder
        return LinearVariable()
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
    ): Variable {
        clientInternalChoice = builder
        return LinearVariable()
    }

    fun <With : Choice> at(
        variable: Variable
    ): MatchBuilder<With> {
        @Suppress("UNCHECKED_CAST")
        return MatchBuilder(serverExternalChoice as ExternalChoiceBuilder<With>)
    }

    fun <With : Choice> to(
        variable: Variable
    ): DotBuilder<With> {
        @Suppress("UNCHECKED_CAST")
        return DotBuilder(clientInternalChoice as InternalChoiceBuilder<With>)
    }
}