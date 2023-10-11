import com.example.demo.DemoApplication
import com.example.demo.service.ItemService
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions.*

@SpringBootTest(classes = [DemoApplication::class])
class ConcurrencyTest @Autowired constructor(
    val itemService: ItemService
) : DescribeSpec({
    describe("동시성 테스트") {
        val requestCount = 150
        context("락을 사용하지 않았을경우") {
            it("동시성에 문제가 생겨 150개의 요청을 보내도 남은 재고가 있다.") {
                runBlocking {
                    val jobs = List(requestCount) {
                        async(Dispatchers.Default) {
                            itemService.purchaseItem("사과")
                        }
                    }
                    jobs.forEach { it.await() }
                }
                val stockCount: Int = itemService.getItemStock("사과")
                assertThat(stockCount).isNotEqualTo(0)
                assertThat(itemService.getFailCount()).isNotEqualTo(50)
            }
        }

        context("스핀락을 사용할 경우") {
            it("동시성이 보장되어 남은 재고갯수가 0개이다. While문을 사용하므로 CPU 사용량이 많다.") {
                runBlocking {
                    val jobs = List(requestCount) {
                        async(Dispatchers.Default) {
                            itemService.purchaseItemWithSpinLock("사과")
                        }
                    }
                    jobs.forEach { it.await() }
                }

                val stockCount: Int = itemService.getItemStock("사과")
                assertThat(stockCount).isEqualTo(0)
                assertThat(itemService.getFailCount()).isEqualTo(50)
            }
        }

        context("Redisson을 사용할 경우") {
            it("동시성이 보장되어 남은 재고갯수가 0개이다. CPU 사용량이 적다") {
                runBlocking {
                    val jobs = List(requestCount) {
                        async(Dispatchers.Default) {
                            itemService.purchaseItemWithRedisson("사과")
                        }
                    }
                    jobs.forEach { it.await() }
                }

                val stockCount: Int = itemService.getItemStock("사과")
                assertThat(stockCount).isEqualTo(0)
                assertThat(itemService.getFailCount()).isEqualTo(50)
            }
        }
    }
})
