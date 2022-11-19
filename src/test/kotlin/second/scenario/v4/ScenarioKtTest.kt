package second.scenario.v4

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScenarioKtTest {

    @Test
    internal fun shouldCreateQueueServer() {
        // given
        val s0 = QS0()
        val s1 = QS1()
        val s2 = QS2 { "foo" }
        val s3 = QS3()
        val s4 = QS4 { println(it) }
        val s5 = Terminal()
        // when
        val queueServer = QueueServer(s0, s1, s2, s3, s4, s5)
        // then
        println(queueServer)
    }

    @Test
    internal fun shouldCreateQueueInvoker() {
        // given
        val qc1 = QC1 { println(it) }
        val qc2 = QC2 { "foo" }
        val qc3 = Done()
        val qc4 = QC4()
        // and
        val queueClient = QueueClient(qc1, qc2, qc3, qc4)
        val qi1 = QI1(queueClient)
        val qi2 = QI2()
        val qi3 = QI3()
        val qi4 = QI4 { "foo" }
        // when
        val queueInvoker = QueueInvoker(qi1, qi2, qi3, qi4, Terminal())
        // then
        println(queueInvoker)
    }

    @Test
    internal fun testYamlNotation() {
        // given
        val input = """
        name: &QS QueueServer
        then: !<their>
          options:
            enq: !<recv>
              what: !<var>
                name: A
              then: !<again>
                name: *QS
            deq: !<our>
              options:
                some: !<send>
                  what:
                    name: A
                  then: !<again>
                    name: *QS
                none: !<close>
                  name: Done
        """.trimIndent()
        // and
        val queueServer = "QueueServer"
        val expectedSessionType = SessionType(
            name = queueServer,
            then = Their(
                choice(
                    "enq" then Recv(
                        Var("A"),
                        Again(queueServer)
                    ),
                    "deq" then Our(
                        choice(
                            "some" then Send(
                                Var("A"),
                                Again(queueServer)
                            ),
                            "none" then Close("Done")
                        )
                    )
                )
            )
        )
        // when
        val actualSessionType = Yaml.default.decodeFromString(SessionType.serializer(), input)
        // then
        assertThat(actualSessionType).isEqualTo(expectedSessionType)
    }
}

@Serializable
data class SessionType(
    val name: String,
    val then: SessionOp
)

@Serializable
sealed class SessionOp

@Serializable
@SerialName("their")
data class Their(
    val options: Map<String, SessionOp>
) : SessionOp()

@Serializable
@SerialName("our")
data class Our(
    val options: Map<String, SessionOp>
) : SessionOp()

@Serializable
@SerialName("recv")
data class Recv(
    val what: Var,
    val then: SessionOp
) : SessionOp()

@Serializable
data class Var(
    val name: String
)

@Serializable
@SerialName("send")
data class Send(
    val what: Var,
    val then: SessionOp
) : SessionOp()

@Serializable
@SerialName("close")
data class Close(
    val name: String
) : SessionOp()

@Serializable
@SerialName("again")
data class Again(
    val name: String
) : SessionOp()

fun choice(vararg pairs: Pair<String, SessionOp>) = mapOf(*pairs)

infix fun <A, B> A.then(that: B): Pair<A, B> = Pair(this, that)