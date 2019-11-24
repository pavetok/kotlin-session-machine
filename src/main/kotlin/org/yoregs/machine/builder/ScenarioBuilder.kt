package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

fun scenario(
    initializer: ScenarioBuilder.() -> Unit
): ScenarioBuilder {
    val scenarioBuilder = ScenarioBuilder()
    initializer.invoke(scenarioBuilder)
    return scenarioBuilder
}

@ScenarioMaker
class ScenarioBuilder {

    private lateinit var serverExternalChoice: ViewpointBuilder
    private lateinit var serverInternalChoice: ViewpointBuilder
    private lateinit var clientExternalChoice: ViewpointBuilder
    private lateinit var clientInternalChoice: ViewpointBuilder

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
        return MatchBuilder(serverExternalChoice.cast())
    }

    fun <With : Choice> to(
        variable: Variable
    ): DotBuilder<With> {
        return DotBuilder(clientInternalChoice.cast())
    }
}