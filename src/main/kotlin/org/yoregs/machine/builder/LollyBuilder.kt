package org.yoregs.machine.builder

import org.yoregs.machine.domain.Key
import org.yoregs.machine.domain.Lollipop

class LollyBuilder<V>(
    private val lollipop: Lollipop<V>
) where V : Any {

    fun from(
        variable: Key<Lollipop<V>>
    ): LollyBuilder<V> {
        return this
    }

    fun receive(
    ): V {
        return "foo" as V
    }
}