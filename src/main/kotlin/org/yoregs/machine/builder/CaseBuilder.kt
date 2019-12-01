package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.Key
import org.yoregs.machine.domain.Lollipop
import org.yoregs.machine.domain.ScenarioMaker

@ScenarioMaker
class CaseBuilder<A>(
) where A : Choice {

    private val endpoints: MutableMap<Key<*>, Any> = mutableMapOf()

    fun <V : Any> from(
        variable: Key<Lollipop<V>>
    ): LollyBuilder<V> {
        return LollyBuilder(variable.cast(endpoints[variable]))
    }

    fun at(
        variable: Key<InternalChoiceBuilder<A>>
    ): DotBuilder<A> {
        return DotBuilder(variable.cast(endpoints[variable]))
    }
}