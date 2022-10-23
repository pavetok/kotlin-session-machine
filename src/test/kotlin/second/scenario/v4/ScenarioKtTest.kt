package second.scenario.v4

import org.junit.jupiter.api.Test

internal class ScenarioKtTest {

    @Test
    internal fun shouldCreateQueueServer() {
        // given
        val s1 = QS1()
        val s2 = QS2 { "foo" }
        val s3 = QS3()
        val s4 = QS4 { it }
        val s5 = Done()
        // when
        val queueServer = QueueServer(s1, s2, s3, s4, s5)
        // then
        println(queueServer)
    }

    @Test
    internal fun shouldCreateQueueInvoker() {
        // given
        val qc1 = QC1()
        val qc2 = QC2({ "foo" }, { it })
        val qc3 = QC3 { "foo" }
        val qc4 = QC4()
        val qc5 = QC5()
        // and
        val qi1 = QueueClient(qc1, qc2, qc3, qc4, qc5)
        val qi2 = QI2 { "foo" }
        // when
        val queueInvoker = QueueInvoker(qi1, qi2, Done())
        // then
        println(queueInvoker)
    }
}