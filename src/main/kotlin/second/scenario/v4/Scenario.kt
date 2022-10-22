package second.scenario.v4

import second.scenario.v4.ClientChoice.DEQ
import second.scenario.v4.ClientChoice.ENQ
import second.scenario.v4.OutcomeChoice.DONE
import second.scenario.v4.OutcomeChoice.RESULT
import second.scenario.v4.ServerChoice.NONE
import second.scenario.v4.ServerChoice.SOME

class QueueServer(
    @State("s1")
    private val s1: QS1,
    @State("s2")
    private val s2: QS2,
    @State("s3")
    private val s3: QS3,
    @State("s4")
    private val s4: QS4,
    @State("s5")
    private val s5: Done
) : Scenario(s1) {
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

class QueueClient(
    @State("s1")
    private val s1: QC1,
    @State("s2")
    private val s2: QC2,
    @State("s3")
    private val s3: QC3,
    @State("s4")
    @Outcome(RESULT)
    private val s4: QC4,
    @State("s5")
    @Outcome(DONE)
    private val s5: Done
) : Scenario(s1) {
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

class QueueInvoker(
    @State("s1")
    private val s1: QueueClient,
    @State("s2")
    private val s2: QI2,
    @State("s3")
    private val s3: Done
) : Scenario(s1) {
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

annotation class State(val name: String)
annotation class Outcome(val value: OutcomeChoice)

class QI2(
    private val produce: () -> String
) : Receiving<Unit, String, Unit> {
    override fun receive(context: Unit) {
        println(produce())
    }
}

class QC1 : Initiating<String, String>, Deciding<String> {
    override fun input(context: String): String {
        return context
    }

    override fun decide(context: String): ClientChoice {
        return ClientChoice.valueOf(context)
    }
}

class QC2(
    private val consume: (String) -> Unit
) : Sending<String, String, Unit> {
    override fun send(context: String) {
        consume(context)
    }
}

class QC3(
    private val produce: () -> String
) : Receiving<Unit, String, String> {
    override fun receive(context: Unit): String {
        return produce()
    }
}

class QC4 : Terminating<String, String> {
    override fun output(context: String): String {
        return context
    }
}

val queueServer = QueueServer(
    QS1(),
    QS2 { "foo" },
    QS3(),
    QS4 { it },
    Done()
)

val queueClient = QueueClient(
    QC1(),
    QC2 { it },
    QC3 { "foo" },
    QC4(),
    Done()
)

class QS1 : Initiating<Unit, List<String>>, Waiting {
    override fun input(context: Unit): List<String> {
        return listOf()
    }
}

class QS2(
    private val produce: () -> String
) : Receiving<List<String>, String, List<String>> {
    override fun receive(context: List<String>): List<String> {
        return context.plus(produce())
    }
}

class QS3(
) : Deciding<List<String>> {
    override fun decide(context: List<String>): ServerChoice {
        if (context.isEmpty()) {
            return NONE
        }
        return SOME
    }
}

class QS4(
    private val consume: (String) -> Unit
) : Sending<List<String>, String, List<String>> {
    override fun send(context: List<String>): List<String> {
        val head = context.first();
        consume(head)
        return context.minus(head)
    }
}

class Done : Terminating<Unit, Unit> {
    override fun output(context: Unit) {
        // do nothing
    }
}

interface Initiating<in P, out Q> {
    fun input(context: P): Q
}

interface Waiting

interface Receiving<in P, in T, out Q> {
    fun receive(context: P): Q
}

interface Deciding<in P> {
    fun decide(context: P): Enum<*>
}

interface Sending<in P, out T, out Q> {
    fun send(context: P): Q
}

interface Terminating<in P, out Q> {
    fun output(context: P): Q
}

fun <S1 : Scenario, S2 : Scenario> S1.invoking(
    scenario: S2,
    configure: S1.() -> Unit
): S1 {
    this.configure()
    return this
}

fun Scenario.our(
    state: Deciding<*>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.their(
    state: Waiting,
    configure: Scenario.() -> Scenario
): Scenario {
    this.configure()
    return this
}

fun Scenario.again(
    state: Waiting
): Scenario {
    return this
}

fun Scenario.choice(
    name: Enum<*>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.outcome(
    name: Enum<*>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun <M> Scenario.receiving(
    state: Receiving<*, M, *>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun <T> Scenario.sending(
    state: Sending<*, T, *>,
    configure: Scenario.() -> Unit
): Scenario {
    this.configure()
    return this
}

fun Scenario.terminating(
    state: Terminating<*, *>
) {
}

abstract class Scenario(
    private val initial: Initiating<*, *>
) {
    constructor(scenario: Scenario) : this(scenario.initial)
}