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

abstract class BuilderScaffold<SELF : BuilderScaffold<SELF>> {
    abstract fun self(): SELF
}

abstract class ExternalChoiceScaffold<SELF : ExternalChoiceScaffold<SELF>> : BuilderScaffold<SELF>()

abstract class ViewpointBuilder : BuilderScaffold<ViewpointBuilder>()