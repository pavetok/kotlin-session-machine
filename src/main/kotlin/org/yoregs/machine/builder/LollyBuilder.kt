package org.yoregs.machine.builder

import org.yoregs.machine.domain.Lollipop
import kotlin.reflect.KClass

class LollyBuilder<T, A>(typeClass: KClass<A>)
        where T : Any,
              A : Lollipop<T> {
    val typeClass: KClass<A> = typeClass
}