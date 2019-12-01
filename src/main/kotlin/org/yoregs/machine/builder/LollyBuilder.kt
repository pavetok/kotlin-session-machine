package org.yoregs.machine.builder

import org.yoregs.machine.domain.Lollipop

class LollyBuilder<V>(
    private val lollipop: Lollipop<V>
) where V : Any {

    fun receive(
    ): V {
        return "foo" as V
    }
}