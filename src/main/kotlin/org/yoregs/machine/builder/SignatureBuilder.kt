package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.Key
import kotlin.reflect.KClass

fun signature(
    initializer: SignatureBuilder.() -> Unit
): SignatureBuilder {
    val signatureBuilder = SignatureBuilder()
    initializer.invoke(signatureBuilder)
    return signatureBuilder
}

class SignatureBuilder {
    val provides: MutableMap<String, Key<*>> = mutableMapOf()
    val has: MutableMap<String, Key<*>> = mutableMapOf()

    fun <With : Choice> server(
        builder: ExternalChoiceBuilder<With>
    ): Key<ExternalChoiceBuilder<With>> {
        return Key()
    }

    fun <With : Choice> client(
        builder: InternalChoiceBuilder<With>
    ): Key<InternalChoiceBuilder<With>> {
        return Key()
    }

    fun <V : Any> value(
        type: KClass<V>
    ): Key<V> {
        return Key<V>()
    }
}