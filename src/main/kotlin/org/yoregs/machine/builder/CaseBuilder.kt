package org.yoregs.machine.builder

import org.yoregs.machine.domain.Choice
import org.yoregs.machine.domain.ScenarioMaker
import org.yoregs.machine.domain.Variable

@ScenarioMaker
class CaseBuilder<With> where With : Choice {

    fun <V : Any> at(
        variable: Variable
    ): LollyBuilder<V> {
        return LollyBuilder()
    }

    fun <Plus : Choice> to(
        variable: Variable
    ): DotBuilder<Plus> {
        return DotBuilder<Plus>()
    }
}