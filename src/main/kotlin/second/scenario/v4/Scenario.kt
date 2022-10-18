package second.scenario.v4

abstract class Scenario(
    private val initial: Initiating<*>
)

class QueueServer(
    private val s1: QS1,
    private val s2: QS2,
    private val s3: QS3,
    private val s4: QS4,
    private val s5: QS5
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
                        terminating(s5)
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
                    terminating()
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

class QC1 : Initiating<String>, Deciding<String> {
    override fun invoke(): String {
        return "enq"
    }

    override fun invoke(p1: String): String {
        return p1
    }
}

class QC2(
    private val send: (String) -> Unit
) : Sending<String, String, Unit> {
    override fun invoke(p1: String) {
        send(p1)
    }
}

class QC3(
    private val receive: () -> String
) : Receiving<Unit, String, String> {
    override fun invoke(p1: Unit): String {
        return receive()
    }
}

class QC4 : Terminating<String>

val queueServer = QueueServer(
    QS1(),
    QS2 { "foo" },
    QS3(),
    QS4 { it },
    QS5()
)

val queueClient = QueueClient(
    QC1(),
    QC2 { it },
    QC3 { "foo" },
    QC4()
)

@State("s1")
class QS1 : Initiating<List<String>>, Waiting {
    override fun invoke(): List<String> {
        return listOf()
    }
}

@State("s2")
class QS2(
    private val receive: () -> String
) : Receiving<List<String>, String, List<String>> {
    override fun invoke(context: List<String>): List<String> {
        return context.plus(receive())
    }
}

@State("s3")
class QS3(
) : Deciding<List<String>> {
    override fun invoke(context: List<String>): String {
        if (context.isEmpty()) {
            return "none"
        }
        return "some"
    }
}

@State("s4")
class QS4(
    private val send: (String) -> Unit
) : Sending<List<String>, String, List<String>> {
    override fun invoke(context: List<String>): List<String> {
        val head = context.first();
        send(head)
        return context.minus(head)
    }
}

@State("s5")
class QS5 : Terminating<Unit>

annotation class State(val name: String)

interface Initiating<out Q> : () -> Q
interface Waiting
interface Receiving<in P, in T, out Q> : (P) -> Q
interface Deciding<in P> : (P) -> String
interface Sending<in P, out T, out Q> : (P) -> Q
interface Terminating<out R>

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
    state: Terminating<*>
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