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
    private val s0: QS0,
    private val s1: QS1,
    private val s2: QS2,
    private val s3: QS3,
    private val s4: QS4,
    private val s5: Done
) : Scenario() {
    init {
        constructing(s0) {
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

    init {
        build()
    }
}

@Name("qc")
data class QueueClient(
    private val s0: QC0,
    private val s1: QC1,
    private val s2: QC2,
    private val s3: QC3,
    private val s4: QC4,
    private val s5: Done
) : Scenario() {
    init {
        constructing(s0) {
            our(s1) {
                choice(ENQ) {
                    sending(s2) {
                        terminating(s5) {
                            DONE
                        }
                    }
                }
                choice(DEQ) {
                    receiving(s3) {
                        terminating(s4) {
                            RESULT
                        }
                    }
                }
            }
        }
    }

    init {
        build()
    }
}

@Name("qi")
data class QueueInvoker(
    private val s0: QI0,
    private val s1: QueueClient,
    private val s2: QI2,
    private val s3: Done
) : Scenario() {
    init {
        constructing(s0) {
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

    init {
        build()
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

@Name("s0")
class QI0(
) : Initiating<Unit, Unit> {
    override fun input(context: Unit) {
        TODO()
    }
}

@Name("s2")
class QI2(
    private val produce: () -> String
) : Receiving<Unit, String, Unit> {
    override fun receive(context: Unit) {
        println(produce())
    }
}

@Name("s0")
class QC0 : Initiating<String, String> {
    override fun input(context: String): String {
        return context
    }
}

@Name("s1")
class QC1 : Deciding<String> {
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

@Name("s0")
class QS0 : Initiating<Unit, List<String>> {
    override fun input(context: Unit): List<String> {
        return listOf()
    }
}

@Name("s1")
class QS1 : Waiting

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

object Noop : Activity

fun Spec.invoking(
    scenario: Activity,
    configure: Spec.() -> Unit
) {
    val name = scenario::class.findAnnotation<Name>()!!.value
    val node = State1(name, scenario)
    node.configure()
    children.add(node)
}

fun Spec.our(
    state: Deciding<*>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun Scenario.constructing(
    state: Initiating<*, *>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    spec = State1(name, state)
    spec.configure()
}

fun Spec.their(
    state: Waiting,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun Spec.choice(
    label: Enum<*>,
    configure: Spec.() -> Unit
) {
    val event = Event(label)
    event.configure()
    children.add(event)
}

fun Spec.outcome(
    label: Enum<*>,
    configure: Spec.() -> Unit
) {
    val event = Event(label)
    event.configure()
    children.add(event)
}

fun <M> Spec.receiving(
    state: Receiving<*, M, *>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun <T> Spec.sending(
    state: Sending<*, T, *>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun Spec.again(
    state: Activity
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State1(name, Noop))
}

fun Spec.terminating(
    state: Terminating<*, *>
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State1(name, state))
}

fun Spec.terminating(
    state: Terminating<*, *>,
    outcome: () -> Enum<*>
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State2(name, state, outcome()))
}

abstract class Scenario(
) : Activity {
    val states: MutableMap<String, Activity> = mutableMapOf()
    val transitions: MutableList<Transition> = mutableListOf()
    val outcomes: MutableMap<String, Enum<*>> = mutableMapOf()
    lateinit var spec: Spec
}

sealed class Spec {
    val children: MutableList<Spec> = mutableListOf()
}

sealed class State(val name: String, val activity: Activity) : Spec()
class State1(name: String, activity: Activity) : State(name, activity)
class State2(name: String, activity: Activity, val label: Enum<*>) : State(name, activity)

data class Event(val label: Enum<*>) : Spec()

sealed class Transition

data class Transition1(val from: String, val to: String) : Transition()
data class Transition2(val from: String, val on: Enum<*>, val to: String) : Transition()

fun Scenario.build() {
    val queue = ArrayDeque<Spec>()
    queue.addFirst(spec)

    while (queue.isNotEmpty()) {
        val spec1 = queue.removeLast()
        when (spec1) {
            is State2 -> {
                states[spec1.name] = spec1.activity
                outcomes[spec1.name] = spec1.label
            }

            is State -> {
                states[spec1.name] = spec1.activity
            }

            is Event -> {}
        }
        for (spec2 in spec1.children) {
            when (spec1) {
                is State -> when (spec2) {
                    is State -> {
                        transitions.add(Transition1(spec1.name, spec2.name))
                    }

                    is Event -> {
                        val spec3 = spec2.children.single() as State
                        transitions.add(Transition2(spec1.name, spec2.label, spec3.name))
                    }
                }

                is Event -> {}
            }
            queue.addFirst(spec2)
        }
    }
}