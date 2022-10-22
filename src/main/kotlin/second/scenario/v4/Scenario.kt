package second.scenario.v4

import second.scenario.v4.ClientChoice.DEQ
import second.scenario.v4.ClientChoice.ENQ
import second.scenario.v4.OutcomeChoice.DONE
import second.scenario.v4.OutcomeChoice.RESULT
import second.scenario.v4.ServerChoice.NONE
import second.scenario.v4.ServerChoice.SOME
import kotlin.reflect.full.findAnnotation

@Name("qs")
data class QueueServer(
    private val s1: QS1,
    private val s2: QS2,
    private val s3: QS3,
    private val s4: QS4,
    private val s5: Done
) : AbstractScenario(s1) {
    init {
        their(s1) {
            choice(ENQ) {
                receiving(s2) {
                    again(s1)
                }
            }
            choice(DEQ) {
                our(s3) {
                    choice(SOME) {
                        sending(s4) {
                            again(s1)
                        }
                    }
                    choice(NONE) {
                        terminating(s5)
                    }
                }
            }
        }
    }
}

@Name("qc")
class QueueClient(
    private val s1: QC1,
    private val s2: QC2,
    private val s3: QC3,
    @Outcome(RESULT)
    private val s4: QC4,
    @Outcome(DONE)
    private val s5: Done
) : AbstractScenario(s1) {
    init {
        our(s1) {
            choice(ENQ) {
                sending(s2) {
                    terminating(s5)
                }
            }
            choice(DEQ) {
                receiving(s3) {
                    terminating(s4)
                }
            }
        }
    }
}

@Name("qi")
class QueueInvoker(
    private val s1: QueueClient,
    private val s2: QI2,
    private val s3: Done
) : AbstractScenario(s1) {
    init {
        invoking(s1) {
            outcome(DONE) {
                terminating(s3)
            }
            outcome(RESULT) {
                receiving(s2) {
                    terminating(s3)
                }
            }
        }
    }
}

enum class ServerChoice {
    SOME, NONE
}

enum class ClientChoice {
    ENQ, DEQ
}

enum class OutcomeChoice {
    DONE, RESULT
}

annotation class Name(val value: String)
annotation class Outcome(val value: OutcomeChoice)

@Name("s2")
class QI2(
    private val produce: () -> String
) : Receiving<Unit, String, Unit> {
    override fun receive(context: Unit) {
        println(produce())
    }
}

@Name("s1")
class QC1 : Initiating<String, String>, Deciding<String> {
    override fun input(context: String): String {
        return context
    }

    override fun decide(context: String): ClientChoice {
        return ClientChoice.valueOf(context)
    }
}

@Name("s2")
class QC2(
    private val consume: (String) -> Unit
) : Sending<String, String, Unit> {
    override fun send(context: String) {
        consume(context)
    }
}

@Name("s3")
class QC3(
    private val produce: () -> String
) : Receiving<Unit, String, String> {
    override fun receive(context: Unit): String {
        return produce()
    }
}

@Name("s4")
class QC4 : Terminating<String, String> {
    override fun output(context: String): String {
        return context
    }
}

val queueClient = QueueClient(
    QC1(),
    QC2 { it },
    QC3 { "foo" },
    QC4(),
    Done()
)

@Name("s1")
class QS1 : Initiating<Unit, List<String>>, Waiting {
    override fun input(context: Unit): List<String> {
        return listOf()
    }
}

@Name("s2")
class QS2(
    private val produce: () -> String
) : Receiving<List<String>, String, List<String>> {
    override fun receive(context: List<String>): List<String> {
        return context.plus(produce())
    }
}

@Name("s3")
class QS3(
) : Deciding<List<String>> {
    override fun decide(context: List<String>): ServerChoice {
        if (context.isEmpty()) {
            return NONE
        }
        return SOME
    }
}

@Name("s4")
class QS4(
    private val consume: (String) -> Unit
) : Sending<List<String>, String, List<String>> {
    override fun send(context: List<String>): List<String> {
        val head = context.first();
        consume(head)
        return context.minus(head)
    }
}

@Name("done")
class Done : Terminating<Unit, Unit> {
    override fun output(context: Unit) {
        // do nothing
    }
}

sealed interface Activity

interface Initiating<in P, out Q> : Activity {
    fun input(context: P): Q
}

interface Waiting : Activity

interface Receiving<in P, in T, out Q> : Activity {
    fun receive(context: P): Q
}

interface Deciding<in P> : Activity {
    fun decide(context: P): Enum<*>
}

interface Sending<in P, out T, out Q> : Activity {
    fun send(context: P): Q
}

interface Terminating<in P, out Q> : Activity {
    fun output(context: P): Q
}

fun <S1 : AbstractScenario, S2 : AbstractScenario> S1.invoking(
    scenario: S2,
    configure: S1.() -> Unit
) {
    this.apply(configure)
}

fun AbstractScenario.our(
    state: Deciding<*>,
    configure: AbstractScenario.() -> Unit
): String {
    val fromState = state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
    this.states[fromState] = state
    this.configure()
    return fromState
}

fun AbstractScenario.their(
    state: Waiting,
    configure: AbstractScenario.() -> Unit
): String {
    val fromState = state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
    this.states[fromState] = state
    this.configure()
    return fromState
}

fun AbstractScenario.again(
    state: Activity
): String {
    return state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
}

fun AbstractScenario.choice(
    label: Enum<*>,
    configure: AbstractScenario.() -> String
) {
    val fromState = "???"
    val toState = this.configure()
}

fun AbstractScenario.outcome(
    label: Enum<*>,
    configure: AbstractScenario.() -> Unit
) {
    this.configure()
}

fun <M> AbstractScenario.receiving(
    state: Receiving<*, M, *>,
    configure: AbstractScenario.() -> String
): String {
    val fromState = state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
    this.states[fromState] = state
    val toState = this.configure()
    return fromState
}

fun <T> AbstractScenario.sending(
    state: Sending<*, T, *>,
    configure: AbstractScenario.() -> String
): String {
    val fromState = state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
    this.states[fromState] = state
    val toState = this.configure()
    return fromState
}

fun AbstractScenario.terminating(
    state: Terminating<*, *>
): String {
    val endState = state::class.findAnnotation<Name>()?.value ?: throw IllegalArgumentException()
    this.states[endState] = state
    return endState
}

abstract class AbstractScenario(
    private val initial: Initiating<*, *>
) {
    val states: MutableMap<String, Activity> = mutableMapOf()
    val transitions: MutableMap<Enum<*>, String> = mutableMapOf()

    constructor(scenario: AbstractScenario) : this(scenario.initial)
}