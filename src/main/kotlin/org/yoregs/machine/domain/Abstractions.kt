package org.yoregs.machine.domain

interface Endpoint
interface Role
interface Choice
interface Variable

class LinearVariable : Variable

interface InternalChoice<T> : Endpoint
interface ExternalChoice<T> : Endpoint
interface Lollipop<T> : Endpoint
interface Tensor<T> : Endpoint

@DslMarker
annotation class ScenarioMaker

abstract class ViewpointBuilder {
    fun <T : ViewpointBuilder> cast(): T {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }
}
