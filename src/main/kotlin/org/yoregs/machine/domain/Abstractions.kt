package org.yoregs.machine.domain

/**
 * Types Correspondence:
 * - InternalChoice: ⊕ = plus = additive disjunction
 * - Tensor: ⊗ = times = multiplicative conjunction
 * - ExternalChoice: & = with = additive conjunction
 * - Par: ⅋ = par = multiplicative disjunction
 * - Lollipop: ⊸ = lolly
 * - Forward: ↔ = identity
 * - Bang: ! = of course
 * - WhyNot: ? = why not
 * - ParallelComposition: | = cut = parallel composition
 *
 * Abstractions Correspondence:
 * - Session Type -> Viewpoint Interface
 * - Process Signature -> Signature Interface
 * - Process Definition -> Scenario Definition (Builder) Class & Instance
 * - Process Instance -> Scenario Instance
 */

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

interface ScenarioView
interface ScenarioSignature
interface ScenarioDefinition<SELF : ScenarioDefinition<SELF>> {
    fun def(): SELF
}

class ExternalChoice<T>
class InternalChoice<T>
class Lollipop<T>
class Tensor<T>
