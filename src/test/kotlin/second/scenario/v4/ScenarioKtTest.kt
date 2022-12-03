package second.scenario.v4

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
        val yaml = """
        what: !<type> QueueServer
        then: !<their>
          choice:
            enq: !<recv>
              what: !<var> A
              then: !<again> QueueServer
            deq: !<our>
              choice:
                some: !<send>
                  what: !<var> A
                  then: !<again> QueueServer
                none: !<close> Done
        """.trimIndent()
        // and
        val queueServer = Type("QueueServer")
        val expectedSessionType = SessionType(
            what = queueServer,
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
        val actualSessionType = Yaml.default.decodeFromString(SessionType.serializer(), yaml)
        // then
        assertThat(actualSessionType).isEqualTo(expectedSessionType)
    }

    @Test
    fun testScalar() {
        // given
        val yaml = """
        then: Done
        """.trimIndent()
        // and
        val expectedValue = Custom1(
            then = Custom2("Done")
        )
        // when
        val actualValue = Yaml.default.decodeFromString(Custom1.serializer(), yaml)
        // then
        assertThat(actualValue).isEqualTo(expectedValue)
    }
}

@Serializable
data class Custom1(
    val then: Custom2
)

@Serializable(with = Custom2Serializer::class)
data class Custom2(
    val name: String
)

object Custom2Serializer : KSerializer<Custom2> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Custom2", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Custom2) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Custom2 {
        return Custom2(decoder.decodeString())
    }
}

@Serializable
data class SessionType(
    val what: Type,
    val then: Behaviour
)

@Serializable
sealed class Behaviour

sealed class Choice : Behaviour()
sealed class Communication : Behaviour()
sealed class Termination : Behaviour()

@Serializable
sealed class Pointer

@Serializable(with = TypeSerializer::class)
data class Type(
    val name: String
) : Pointer()

@Serializable
@SerialName("their")
data class Their(
    val choice: Map<String, Behaviour>
) : Choice()

@Serializable
@SerialName("our")
data class Our(
    val choice: Map<String, Behaviour>
) : Choice()

@Serializable
@SerialName("recv")
data class Recv(
    val what: Pointer,
    val then: Behaviour
) : Communication()

@Serializable
@SerialName("send")
data class Send(
    val what: Pointer,
    val then: Behaviour
) : Communication()

@Serializable(with = CloseSerializer::class)
data class Close(
    val name: String
) : Termination()

@Serializable(with = AgainSerializer::class)
data class Again(
    val type: Type
) : Termination()

@Serializable(with = VarSerializer::class)
data class Var(
    val name: String
) : Pointer()

fun choice(vararg pairs: Pair<String, Behaviour>) = mapOf(*pairs)

infix fun <A, B> A.then(that: B): Pair<A, B> = Pair(this, that)

object CloseSerializer : KSerializer<Close> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("close", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Close) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Close {
        return Close(decoder.decodeString())
    }
}

@OptIn(ExperimentalSerializationApi::class)
object AgainSerializer : KSerializer<Again> {
    private val delegateSerializer = TypeSerializer
    override val descriptor: SerialDescriptor = SerialDescriptor("again", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Again) {
        encoder.encodeSerializableValue(delegateSerializer, value.type)
    }

    override fun deserialize(decoder: Decoder): Again {
        return Again(decoder.decodeSerializableValue(delegateSerializer))
    }
}

object TypeSerializer : KSerializer<Type> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("type", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Type) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Type {
        return Type(decoder.decodeString())
    }
}

object VarSerializer : KSerializer<Var> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("var", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Var) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Var {
        return Var(decoder.decodeString())
    }
}