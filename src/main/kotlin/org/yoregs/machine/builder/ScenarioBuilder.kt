package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.LinearVariable
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

fun <With, Plus> scenario(
    initializer: ScenarioBuilder<With, Plus>.() -> Unit
): ScenarioBuilder<With, Plus> where With : Choice, Plus : Choice {
    val scenarioBuilder = ScenarioBuilder<With, Plus>()
    initializer.invoke(scenarioBuilder)
    return scenarioBuilder
}

@ScenarioMaker
class ScenarioBuilder<With, Plus> where With : Choice, Plus : Choice {

    lateinit var serverExternalChoice: ExternalChoiceBuilder<With>
    lateinit var serverInternalChoice: InternalChoiceBuilder<Plus>

    lateinit var clientExternalChoice: ExternalChoiceBuilder<Plus>
    lateinit var clientInternalChoice: InternalChoiceBuilder<With>

    fun server(
        builder: ExternalChoiceBuilder<With>
    ): Variable {
        serverExternalChoice = builder
        return LinearVariable()
    }

    fun server(
        builder: InternalChoiceBuilder<Plus>
    ): Variable {
        serverInternalChoice = builder
        return LinearVariable()
    }

    fun client(
        builder: ExternalChoiceBuilder<Plus>
    ): Variable {
        clientExternalChoice = builder
        return LinearVariable()
    }

    fun client(
        builder: InternalChoiceBuilder<With>
    ): Variable {
        clientInternalChoice = builder
        return LinearVariable()
    }

    fun from(
        variable: Variable
    ): MatchBuilder<With> {
        return MatchBuilder(serverExternalChoice)
    }

    fun to(
        variable: Variable
    ): DotBuilder<With> {
        return DotBuilder(clientInternalChoice)
    }
}