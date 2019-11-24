package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker

@ScenarioMaker
class MatchBuilder<With>(externalChoice: ExternalChoiceBuilder<With>) where With : Choice {

    val externalChoice = externalChoice

    fun match(
        initializer: MatchBuilder<With>.() -> Unit
    ) {
    }

    fun case(
        case: With,
        initializer: CaseBuilder<With>.() -> Unit
    ) {
    }

    fun <V : Any> dot(
        case: With,
        initializer: TensorBuilder<V>.() -> Unit
    ) {
    }
}