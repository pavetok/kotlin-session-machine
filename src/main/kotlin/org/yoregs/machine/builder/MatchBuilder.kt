package org.yoregs.machine.builder

import org.yoregs.machine.domain.*

@ScenarioMaker
class MatchBuilder<With>(

    private val externalChoice: ExternalChoiceBuilder<With>

) where With : Choice {

    private val endpoints: MutableMap<Key<*>, Any> = mutableMapOf()

    // code gen
    lateinit var queue: Key<Lollipop<String>>

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
        variable: Key<Lollipop<V>>
    ): LollyBuilder<V> {
        return LollyBuilder(variable.cast(endpoints[variable]))
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