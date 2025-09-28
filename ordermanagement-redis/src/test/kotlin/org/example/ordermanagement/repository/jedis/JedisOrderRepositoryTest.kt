package org.example.ordermanagement.repository.jedis

import kotlinx.serialization.json.Json
import org.example.ordermanagement.entities.Cart
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.entities.Position
import org.example.ordermanagement.entities.Product
import org.junit.jupiter.api.*
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.Jedis
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JedisOrderRepositoryTest {

    companion object {
        private lateinit var redisContainer: GenericContainer<Nothing>

        @JvmStatic
        @BeforeAll
        fun initAll() {

            // Create Redis Container (it uses Docker under the hood)
            redisContainer = GenericContainer<Nothing>("redis:7.2.3")
                .withExposedPorts(6379)

            // Start the Container
            redisContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            redisContainer.stop()
        }
    }

    private lateinit var jedis: Jedis
    private lateinit var repository: JedisOrderRepository
    private val orderId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000")

    @BeforeEach
    fun setup() {
        jedis = Jedis(redisContainer.host, redisContainer.getMappedPort(6379))
        repository = JedisOrderRepository(jedis = jedis)
    }

    @AfterEach
    fun tearDown() {
        jedis.close()
    }

    @Test
    fun byId() {
        // Given
        val orderJson = javaClass.classLoader.getResourceAsStream("order.json")?.use {
            InputStreamReader(it).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    bufferedReader.readText()
                }
            }
        }
        jedis.set("orders_${orderId}", orderJson)

        // When
        val order = repository.byId(orderId)

        // Then
        assertNotNull(order)
        assertEquals(orderId, order.id)
        assertEquals(1, order.cart.positions.count())
        assertEquals("0123456789123", order.cart.positions[0].product.ean)
        assertEquals(3, order.cart.positions[0].quantity)
        assertEquals(9.99, order.cart.positions[0].product.price)
    }

    @Test
    fun save() {
        // Given
        val order = Order(id = orderId, cart = Cart(positions = mutableListOf(
            Position(product = Product(ean = "0123456789123", price = 9.99), quantity = 3)
        )))

        // When
        repository.save(order)

        // Then
        val orderJson = jedis.get("orders_${order.id}")
        val expected = javaClass.classLoader.getResourceAsStream("order.json")?.use {
            InputStreamReader(it).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    bufferedReader.readText()
                }
            }
        }
        assertJsonStringsEqual(expected!!, orderJson)
    }

    private fun assertJsonStringsEqual(expected: String, actual: String) {
        val expectedJson = Json.decodeFromString<OrderRedisModel>(expected)
        val actualJson = Json.decodeFromString<OrderRedisModel>(actual)

        assertEquals(expectedJson, actualJson)
    }
}
