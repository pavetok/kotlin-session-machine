package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.LinearVariable
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable
import kotlin.reflect.KClass

fun <With, Plus> scenario(
    initializer: ScenarioBuilder<With, Plus>.() -> Unit
): ScenarioBuilder<With, Plus> where With : Choice, Plus : Choice {
    val scenarioBuilder = ScenarioBuilder<With, Plus>()
    initializer.invoke(scenarioBuilder)
    return scenarioBuilder
}

@ScenarioMaker
class ScenarioBuilder<With, Plus> where With : Choice, Plus : Choice {

    lateinit var externalChoiceType: KClass<With>

    fun view(
        viewpointBuilder: ViewpointBuilder<With, Plus>
    ): Variable {
        externalChoiceType = viewpointBuilder.externalChoiceType
        return LinearVariable()
    }

    fun weiv(
        viewpointBuilder: ViewpointBuilder<Plus, With>
    ): Variable {
        return LinearVariable()
    }

    fun <With : Choice> at(
        variable: Variable
    ): MatchBuilder<With> {
        return MatchBuilder()
    }
}