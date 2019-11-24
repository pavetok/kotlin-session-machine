package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class MatchBuilder<With> where With : Choice {

    fun match(
        initializer: MatchBuilder<With>.() -> Unit
    ): MatchBuilder<With> {
        return this.apply(initializer)
    }

    fun <Case : With> case(
        case: Case,
        initializer: CaseBuilder<Case>.() -> Unit
    ): CaseBuilder<Case> {
        return CaseBuilder()
    }

    fun at(
        variable: Variable
    ): MatchBuilder<With> {
        return MatchBuilder()
    }

    fun to(
        variable: Variable
    ): MatchBuilder<With> {
        return MatchBuilder()
    }

    fun receive(
    ): String {
        return "foo"
    }

    fun <V : String> dot(
        case: With,
        initializer: TensorBuilder<V>.() -> Unit
    ): TensorBuilder<V> {
        return TensorBuilder<V>()
    }
}