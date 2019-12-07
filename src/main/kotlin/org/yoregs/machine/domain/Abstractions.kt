package org.yoregs.machine.domain

interface Endpoint
interface Role
interface Choice
interface Variable

class LinearVariable : Variable

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

interface ScenarioViewpoint
interface ScenarioSignature
interface ScenarioDefinition

class ExternalChoice<T>
class InternalChoice<T>
class Lollipop<T>
class Tensor<T>
