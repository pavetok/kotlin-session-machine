package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class MatchBuilder<With>(
    private val externalChoice: ExternalChoiceBuilder<With>
) where With : Choice {

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

    fun <Plus : Choice> at(
        variable: Variable
    ): DotBuilder<Plus> {
        return DotBuilder(externalChoice.internalChoice.cast())
    }
}