package org.example.ordermanagement.repository.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.ordermanagement.entities.Cart
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.entities.Position
import org.example.ordermanagement.entities.Product
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.postgresql.util.PGobject
import org.testcontainers.containers.PostgreSQLContainer
import java.util.*
import javax.sql.DataSource
import kotlin.test.*

class JdbcOrderRepositoryTest {

    // We need to use a companion object here, because we are using @JvmStatic
    companion object {
        private lateinit var postgresContainer: PostgreSQLContainer<Nothing>
        private lateinit var dataSource: DataSource

        @JvmStatic
        @BeforeAll
        fun initAll() {

            // Create PostgreSQL Container (it uses Docker under the hood)
            postgresContainer = PostgreSQLContainer<Nothing>("postgres:16").apply {
                withDatabaseName("order_management")
                withUsername("user")
                withPassword("pass")
                withExposedPorts(5432)
                withInitScript("db/init.sql")
            }

            // Start the Container (which is basically docker run ....)
            postgresContainer.start()

            // We use Hikari to create a DataSource
            val config = HikariConfig().apply {
                jdbcUrl = postgresContainer.jdbcUrl
                username = postgresContainer.username
                password = postgresContainer.password
                driverClassName = "org.postgresql.Driver"
            }

            dataSource = HikariDataSource(config)
        }

        @AfterAll
        @JvmStatic
        fun tearDownAll() {
            postgresContainer.stop()
        }
    }

    private lateinit var repository: JdbcOrderRepository

    private val orderId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000")

    @BeforeEach
    fun init() {
        repository = JdbcOrderRepository(dataSource = dataSource)
    }

    @AfterEach
    fun tearDown() {
        // Clear Database of orders
        dataSource.connection.prepareStatement("DELETE FROM orders").execute()
    }

    @Test
    fun `byId() returns null if order does not exist`() {
        // When
        val order = repository.byId(orderId)

        // Then
        assertNull(order)
    }

    @Test
    fun `byId() returns order if exist`() {
        // Given
        val sql = "INSERT INTO orders (id, positions) VALUES (?, ?)"
        val prepareStatement = dataSource.connection.prepareStatement(sql)
        val positions = arrayOf(
            PositionDbModel(
                ean = "123",
                price = 9.99,
                quantity = 2
            )
        )
        val jsonObject = PGobject()
        jsonObject.type = "json"
        jsonObject.value = Json.encodeToString(positions)

        prepareStatement.setObject(1, orderId)
        prepareStatement.setObject(2, jsonObject)
        prepareStatement.executeUpdate()

        // When
        val order = repository.byId(orderId)

        // Then
        assertNotNull(order)
        assertEquals(1, order.cart.positions.count())
    }

    @Test
    fun `save() creates new order if not exists`() {
        // Given
        val order = Order(
            id = orderId,
            cart = Cart(
                positions = mutableListOf(
                    Position(
                        product = Product(ean = "0123456789123", price = 9.99),
                        quantity = 3
                    )
                )
            )
        )

        // When
        repository.save(order)

        // Then
        assertOrdersCount(1)
    }

    @Test
    fun `save() updates order`() {
        // Given
        val order = Order(
            id = orderId,
            cart = Cart(
                positions = mutableListOf(
                    Position(
                        product = Product(ean = "123", price = 9.99),
                        quantity = 1
                    )
                )
            )
        )

        // When
        repository.save(order)
        order.addToCart(Product(ean = "124", price = 19.99), quantity = 2)
        repository.save(order)

        // Then
        val orderFromDatabase = repository.byId(order.id)

        assertNotNull(orderFromDatabase)
        assertEquals(2, orderFromDatabase.cart.positions.count())

        assertEquals("123", orderFromDatabase.cart.positions[0].product.ean)
        assertEquals(1, orderFromDatabase.cart.positions[0].quantity)
        assertEquals(9.99, orderFromDatabase.cart.positions[0].product.price)

        assertEquals("124", orderFromDatabase.cart.positions[1].product.ean)
        assertEquals(2, orderFromDatabase.cart.positions[1].quantity)
        assertEquals(19.99, orderFromDatabase.cart.positions[1].product.price)

        assertOrdersCount(1)
    }

    @Test
    fun `save() creates two different order`() {
        val order1 = Order(
            id = UUID.fromString("550e8400-e29b-11d4-a716-446655440000"),
            cart = Cart(
                positions = mutableListOf(
                    Position(
                        product = Product(ean = "0123456789123", price = 9.99),
                        quantity = 3
                    )
                )
            )
        )

        val order2 = Order(
            id = UUID.fromString("12345678-1234-1234-1234-123456789012"),
            cart = Cart(
                positions = mutableListOf(
                    Position(
                        product = Product(ean = "0123456789123", price = 9.99),
                        quantity = 3
                    )
                )
            )
        )

        repository.save(order1)
        repository.save(order2)

        assertOrdersCount(2)
    }

    private fun assertOrdersCount(count: Int) {
        val result = dataSource.connection
            .prepareStatement("SELECT COUNT(id) as count FROM orders")
            .executeQuery()

        assertTrue(result.next())
        assertEquals(count, result.getInt("count"))
    }
}
