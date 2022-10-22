package second.scenario.v4

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
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
        assertThat(queueServer.states)
            .containsExactly(
                entry("s1", s1),
                entry("s2", s2),
                entry("s3", s3),
                entry("s4", s4),
                entry("done", s5)
            )
    }
}