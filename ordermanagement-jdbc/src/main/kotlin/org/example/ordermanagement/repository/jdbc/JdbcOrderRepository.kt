package org.example.ordermanagement.repository.jdbc

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.ordermanagement.entities.Cart
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.entities.Position
import org.example.ordermanagement.entities.Product
import org.example.ordermanagement.repositories.OrderRepository
import org.postgresql.util.PGobject
import java.util.*
import javax.sql.DataSource

class JdbcOrderRepository(private val dataSource: DataSource) : OrderRepository {

    override fun byId(id: UUID): Order? {
        val sql = "SELECT id, positions FROM orders WHERE id = ?"
        val connection = dataSource.connection
        val statement = connection.prepareStatement(sql)
        statement.setObject(1, id)

        val result = statement.executeQuery()
        var order: Order? = null

        if (result.next()) {
            val positions: List<PositionDbModel> = Json.decodeFromString(result.getString("positions"))
            val cartPositions = positions
                .map {
                    Position(
                        product = Product(ean = it.ean, price = it.price),
                        quantity = it.quantity
                    )
                }.toMutableList()

            order = Order(
                id = UUID.fromString(result.getString("id")),
                cart = Cart(positions = cartPositions)
            )
        }

        result.close()
        statement.close()
        connection.close()

        return order
    }

    override fun save(order: Order) {
        val positionsDbModel = order.cart.positions.map {
            PositionDbModel(ean = it.product.ean, quantity = it.quantity, price = it.product.price)
        }
        val orderDbModel = OrderDbModel(
            id = order.id,
            positions = positionsDbModel
        )

        val sql = "INSERT INTO orders (id, positions) " +
                "VALUES (?, ?) " +
                "ON CONFLICT (id) " +
                "DO UPDATE set positions = ?"

        val connection = dataSource.connection
        val statement = connection.prepareStatement(sql)
        statement.setObject(1, orderDbModel.id)

        // JSONB Fields need special treatment.
        val jsonObject = PGobject()
        jsonObject.type = "json"
        jsonObject.value = Json.encodeToString(positionsDbModel)
        statement.setObject(2, jsonObject)
        statement.setObject(3, jsonObject)

        statement.executeUpdate()

        statement.close()
        connection.close()
    }
}
