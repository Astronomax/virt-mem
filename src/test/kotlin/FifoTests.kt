import org.junit.jupiter.api.*
import ru.emkn.kotlin.FIFO
import java.io.File
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals

class FifoTests {
    @TestFactory
    fun `multiple test`(): Stream<DynamicTest> {
        val tests = File("./src/test/kotlin/fifoTests").listFiles()
        return IntStream.range(0, tests.size).mapToObj { i ->
            DynamicTest.dynamicTest("Test $i") {
                val reader = tests[i].useLines { it.toList() }

                val n = reader[0].split(" ")[0].toInt()
                val m = reader[0].split(" ")[1].toInt()
                val order = reader[1].split(" ").map { it.toInt() - 1 }
                val ans = reader[2].split(" ").map { it.toInt() }

                val Fifo = FIFO(n, m)
                assertEquals(ans, order.map { Fifo.query(it) })
            }
        }
    }
}