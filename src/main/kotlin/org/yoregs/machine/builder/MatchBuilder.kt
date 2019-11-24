package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class MatchBuilder<With>(externalChoice: ExternalChoiceBuilder<With>) where With : Choice {

    val externalChoice = externalChoice

    fun match(
        initializer: MatchBuilder<With>.() -> Unit
    ) {
    }

    fun case(
        case: With,
        initializer: MatchBuilder<With>.() -> Unit
    ) {
    }

    fun <V : Any> from(
        variable: Variable
    ): LollyBuilder<V> {
        return LollyBuilder()
    }

    fun <Plus : Choice> to(
        variable: Variable
    ): DotBuilder<Plus> {
        return DotBuilder(externalChoice.internalChoice.cast())
    }
}