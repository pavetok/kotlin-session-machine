package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

@ScenarioMaker
class MatchBuilder<With>(

    private val externalChoice: ExternalChoiceBuilder<With>

) where With : Choice {

    private val endpoints: MutableMap<Key<*>, Any> = mutableMapOf()

    fun match(
        initializer: MatchBuilder<With>.() -> Unit
    ) {
    }

    fun <V : Any> case(
        case: With,
        initializer: CaseBuilder<With>.(key: Key<Lollipop<V>>) -> Unit
    ) {
    }

    fun <Plus : Choice> esac(
        case: With,
        initializer: CaseBuilder<Plus>.(key: Key<InternalChoiceBuilder<Plus>>) -> Unit
    ) {
    }

    fun <Plus : Choice> at(
        variable: Variable
    ): DotBuilder<Plus> {
        @Suppress("UNCHECKED_CAST")
        return DotBuilder(externalChoice.internalChoice as InternalChoiceBuilder<Plus>)
    }
}