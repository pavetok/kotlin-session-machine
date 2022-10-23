package second.scenario.v4

import org.junit.jupiter.api.Test

internal class ScenarioKtTest {

    @Test
    internal fun shouldCreateQueueServer() {
        // given
        val s0 = QS0()
        val s1 = QS1()
        val s2 = QS2 { "foo" }
        val s3 = QS3()
        val s4 = QS4 { it }
        val s5 = Done()
        // when
        val queueServer = QueueServer(s0, s1, s2, s3, s4, s5)
        // then
        println(queueServer)
    }

    @Test
    internal fun shouldCreateQueueInvoker() {
        // given
        val qc0 = QC0()
        val qc1 = QC1()
        val qc2 = QC2 { it }
        val qc3 = QC3 { "foo" }
        val qc4 = QC4()
        // and
        val qi0 = QI0()
        val qi1 = QueueClient(qc0, qc1, qc2, qc3, qc4, Done())
        val qi2 = QI2 { "foo" }
        // when
        val queueInvoker = QueueInvoker(qi0, qi1, qi2, Done())
        // then
        println(queueInvoker)
    }
}