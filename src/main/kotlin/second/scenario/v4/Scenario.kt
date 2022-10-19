package second.scenario.v4

abstract class Scenario(
    private val initial: Initiating<*, *>
)

class QueueServer(
    private val s1: QS1,
    private val s2: QS2,
    private val s3: QS3,
    private val s4: QS4
) : Scenario(s1) {
    init {
        their(s1) {
            choice("enq") {
                receiving(s2) {
                    again(s1)
                }
            }
            choice("deq") {
                our(s3) {
                    choice("some") {
                        sending(s4) {
                            again(s1)
                        }
                    }
                    choice("none") {
                        terminating(Done)
                    }
                }
            }
        }
    }
}

class QueueClient(
    private val s1: QC1,
    private val s2: QC2,
    private val s3: QC3,
    private val s4: QC4
) : Scenario(s1) {
    init {
        our(s1) {
            choice("enq") {
                sending(s2) {
                    terminating(Done)
                }
            }
            choice("deq") {
                receiving(s3) {
                    terminating(s4)
                }
            }
        }
    }
}

class QC1 : Initiating<Unit, String>, Deciding<String> {
    override fun input(context: Unit): String {
        return "enq"
    }

    override fun decide(context: String): String {
        return context
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
    QS4 { it }
)

val queueClient = QueueClient(
    QC1(),
    QC2 { it },
    QC3 { "foo" },
    QC4()
)

@State("s1")
class QS1 : Initiating<Unit, List<String>>, Waiting {
    override fun input(context: Unit): List<String> {
        return listOf()
    }
}

@State("s2")
class QS2(
    private val produce: () -> String
) : Receiving<List<String>, String, List<String>> {
    override fun receive(context: List<String>): List<String> {
        return context.plus(produce())
    }
}

@State("s3")
class QS3(
) : Deciding<List<String>> {
    override fun decide(context: List<String>): String {
        if (context.isEmpty()) {
            return "none"
        }
        return "some"
    }
}

@State("s4")
class QS4(
    private val consume: (String) -> Unit
) : Sending<List<String>, String, List<String>> {
    override fun send(context: List<String>): List<String> {
        val head = context.first();
        consume(head)
        return context.minus(head)
    }
}

object Done : Terminating<Unit, Unit> {
    override fun output(context: Unit) {
        // do nothing
    }
}

annotation class State(val name: String)

interface Initiating<in P, out Q> {
    fun input(context: P): Q
}

interface Waiting

interface Receiving<in P, in T, out Q> {
    fun receive(context: P): Q
}

interface Deciding<in P> {
    fun decide(context: P): String
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
    name: String,
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

fun Scenario.terminating(
) {
}

fun Scenario.terminating(
    name: String
) {
}

class C1
class C2
class C3
class C4
class C5

class M1
class M2