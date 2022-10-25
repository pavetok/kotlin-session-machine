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
        val qi0 = QI0()
        val qi1 = QueueClient(qc1, qc2, qc3, qc4)
        val qi2 = QI2 { "foo" }
        // when
        val queueInvoker = QueueInvoker(qi0, qi1, qi2, Terminal())
        // then
        println(queueInvoker)
    }
}