import com.example.demo.DemoApplication
import com.example.demo.service.ItemService
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean

@SpringBootTest(classes = [DemoApplication::class])
class ConcurrencyTest @Autowired constructor(
    val itemService: ItemService
) : DescribeSpec({
    describe("동시성 테스트") {
        val requestCount = 150
        context("분산락을 사용하지 않았을경우") {
            it("동시성에 문제가 생긴다.") {
                runBlocking {
                    val jobs = List(requestCount) {
                        async(Dispatchers.Default) {
                            itemService.purchaseItemWithNoLock("사과")
                        }
                    }
                    jobs.forEach { it.await() }
                }
                itemService.printItemStock("사과")
            }
        }
        fun getMemoryUsage(): Long {
            val memoryBean: MemoryMXBean = ManagementFactory.getMemoryMXBean()
            return memoryBean.heapMemoryUsage.used
        }

        context("스피락을 사용할 경우") {
            it("메모리 사용량을 측정한다.") {
                val beforeMemory = getMemoryUsage()
                runBlocking {
                    val jobs = List(requestCount) {
                        async(Dispatchers.Default) {
                            itemService.purchaseItemWithNoLock("사과")
                        }
                    }
                    jobs.forEach { it.await() }
                }
                itemService.printItemStock("사과")
                val afterMemory = getMemoryUsage()
                val usedMemory = afterMemory - beforeMemory
                println("Used memory: $usedMemory bytes")
            }
        }
    }
})
