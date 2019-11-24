package org.yoregs.machine.builder

import kotlin.reflect.KClass

class LollyBuilder<T> where T : Any {

    fun receive(
        typeClass: KClass<T>
    ): T {
        return "foo" as T
    }
}