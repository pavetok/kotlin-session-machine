package org.yoregs.machine.builder

import org.yoregs.machine.domain.Tensor
import kotlin.reflect.KClass

open class TensorBuilder<T, A>(typeClass: KClass<A>)
        where T : Any,
              A : Tensor<T> {
    val typeClass: KClass<A> = typeClass
}