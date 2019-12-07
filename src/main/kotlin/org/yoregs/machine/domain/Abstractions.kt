package org.yoregs.machine.domain

interface Endpoint
interface Role
interface Choice
interface Choice2<T>
interface Variable

class LinearVariable : Variable

interface InternalChoice<T> : Endpoint
interface ExternalChoice<T> : Endpoint
class Lollipop<T> : Endpoint
interface Tensor<T> : Endpoint

@DslMarker
annotation class ScenarioMaker

abstract class ViewpointBuilder

class Key<T : Any> {
    fun cast(value: Any?): T {
        return value as T
    }

    fun self(): Key<T> {
        return this
    }
}

interface ScenarioViewpoint {
}

interface ScenarioSignature
interface ScenarioDefinition