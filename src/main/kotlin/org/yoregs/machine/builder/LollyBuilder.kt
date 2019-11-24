package org.yoregs.machine.builder

import kotlin.reflect.KClass

class LollyBuilder<V>() where V : Any {

    fun receive(
        typeClass: KClass<V>
    ): V {
        return "foo" as V
    }
}