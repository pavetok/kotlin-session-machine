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
    private val s5: Terminal
) : Scenario(s0) {
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

    init {
        build()
    }
}

@Name("qc")
data class QueueClient(
    private val s1: QC1,
    private val s2: QC2,
    private val s3: Done,
    private val s4: QC4
) : Scenario() {
    init {
        inputting {
            choice(ENQ) {
                sending(s1) {
                    outputting(s3) {
                        DONE
                    }
                }
            }
            choice(DEQ) {
                receiving(s2) {
                    outputting(s4) {
                        RESULT
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
    private val s3: Terminal
) : Scenario(s0) {
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
) : Initiating<ClientChoice> {
    override fun initiate(): ClientChoice {
        return ENQ // а где элемент?
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

@Name("s1")
class QC1(
    private val consume: (String) -> Unit
) : Sending<String, String, Unit> {
    override fun send(context: String) {
        consume(context)
    }
}

@Name("s2")
class QC2(
    private val produce: () -> String
) : Receiving<Unit, String, String> {
    override fun receive(context: Unit): String {
        return produce()
    }
}

@Name("s4")
class QC4(
) : Outputting<String, String> {
    override fun output(context: String): String {
        return context
    }
}

@Name("s0")
class QS0(
) : Initiating<List<String>> {
    override fun initiate(): List<String> {
        return listOf()
    }
}

@Name("s1")
class QS1(
) : Waiting<ClientChoice>

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
) : Deciding<List<String>, ServerChoice> {
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

@Name("terminal")
class Terminal : Terminating<Any> {
    override fun terminate(context: Any) {
        // do nothing
    }
}

@Name("done")
class Done : Outputting<Any, Unit> {
    override fun output(context: Any) {
        // do nothing
    }
}

sealed interface Activity

interface Initiating<out T> : Activity {
    fun initiate(): T
}

interface Waiting<L : Enum<L>> : Activity

interface Receiving<in A, in M, out B> : Activity {
    fun receive(context: A): B
}

interface Deciding<in A, out L : Enum<out L>> : Activity {
    fun decide(context: A): L
}

interface Sending<in A, out M, out B> : Activity {
    fun send(context: A): B
}

interface Terminating<in T> : Activity {
    fun terminate(context: T)
}

interface Outputting<in A, out B> : Activity {
    fun output(context: A): B
}

object Noop : Activity

fun Scenario.invoking(
    scenario: Activity,
    configure: Spec.() -> Unit
) {
    val name = scenario::class.findAnnotation<Name>()!!.value
    spec = State1(name, scenario)
    spec.configure()
}

fun Spec.invoking(
    scenario: Activity,
    configure: Spec.() -> Unit
) {
    val name = scenario::class.findAnnotation<Name>()!!.value
    val node = State1(name, scenario)
    node.configure()
    children.add(node)
}

fun Scenario.our(
    state: Activity,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    spec = State1(name, state)
    spec.configure()
}

fun Scenario.inputting(
    configure: Spec.() -> Unit
) {
    spec = Input
    spec.configure()
}

fun <A, L : Enum<L>> Spec.our(
    state: Deciding<A, L>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun Scenario.their(
    state: Activity,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    spec = State1(name, state)
    spec.configure()
}

fun Spec.their(
    state: Activity,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun <L : Enum<L>> Spec.choice(
    label: L,
    configure: Spec.() -> Unit
) {
    val node = Event(label)
    node.configure()
    children.add(node)
}

fun <L : Enum<L>> Spec.outcome(
    label: L,
    configure: Spec.() -> Unit
) {
    val node = Event(label)
    node.configure()
    children.add(node)
}

fun <A, M, B> Spec.receiving(
    state: Receiving<A, M, B>,
    configure: Spec.() -> Unit
) {
    val name = state::class.findAnnotation<Name>()!!.value
    val node = State1(name, state)
    node.configure()
    children.add(node)
}

fun <A, M, B> Spec.sending(
    state: Sending<A, M, B>,
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

fun <T> Spec.terminating(
    state: Terminating<T>
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State1(name, state))
}

fun <T, L : Enum<L>> Spec.terminating(
    state: Terminating<T>,
    outcome: () -> L
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State2(name, state, outcome()))
}

fun <A, B, L : Enum<L>> Spec.outputting(
    state: Outputting<A, B>,
    outcome: () -> L
) {
    val name = state::class.findAnnotation<Name>()!!.value
    children.add(State2(name, state, outcome()))
}

abstract class Scenario(
    private val constructor: Initiating<*> = object : Initiating<Unit> {
        override fun initiate() = Unit
    }
) : Activity {
    lateinit var spec: Spec
    val inputs: MutableMap<Enum<*>, String> = mutableMapOf()
    val states: MutableMap<String, Activity> = mutableMapOf()
    val transitions: MutableList<Transition> = mutableListOf()
    val outputs: MutableMap<String, Enum<*>> = mutableMapOf()
}

sealed class Spec {
    val children: MutableList<Spec> = mutableListOf()
}

object Input : Spec()

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
                outputs[spec1.name] = spec1.label
            }

            is State -> {
                states[spec1.name] = spec1.activity
            }

            else -> {}
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

                    else -> {}
                }

                Input -> when (spec2) {
                    is Event -> {
                        val spec3 = spec2.children.single() as State
                        inputs[spec2.label] = spec3.name
                    }

                    else -> {}
                }

                else -> {}
            }
            queue.addFirst(spec2)
        }
    }
}