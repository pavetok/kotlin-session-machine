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

abstract class ViewpointBuilder

class Key<T : Any> {
    fun cast(value: Any?): T {
        return value as T
    }
}