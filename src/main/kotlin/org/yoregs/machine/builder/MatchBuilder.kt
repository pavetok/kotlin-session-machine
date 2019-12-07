package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.Key
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class MatchBuilder<With>(
    private val externalChoice: ExternalChoiceBuilder<With>
) where With : Choice {
    private val endpoints: MutableMap<Key<*>, Any> = mutableMapOf()

    fun match(
        initializer: MatchBuilder<With>.() -> Unit
    ) {
    }

    fun <Plus : Choice> esac(
        case: With,
        initializer: CaseBuilder<Plus>.(key: Key<InternalChoiceBuilder<Plus>>) -> Unit
    ) {
    }

    fun case(
        case: With,
        initializer: CaseBuilder<With>.(key: Key<InternalChoiceBuilder<With>>) -> Unit
    ) {
    }

    fun <Plus : Choice> at(
        variable: Variable
    ): DotBuilder<Plus> {
        @Suppress("UNCHECKED_CAST")
        return DotBuilder(externalChoice.internalChoice as InternalChoiceBuilder<Plus>)
    }

    fun <Plus : Choice> at(
        variable: Key<InternalChoiceBuilder<Plus>>
    ): DotBuilder<Plus> {
        return DotBuilder(variable.cast(endpoints[variable]))
    }
}