package org.example.ordermanagement.repository.jedis

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.ordermanagement.entities.Cart
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.entities.Position
import org.example.ordermanagement.entities.Product
import org.example.ordermanagement.repositories.OrderRepository
import redis.clients.jedis.Jedis
import java.util.*

class JedisOrderRepository(private val jedis: Jedis) : OrderRepository {

    override fun byId(id: UUID): Order? {
        val result = jedis["orders_$id"] ?: return null
        val redisModel: OrderRedisModel = Json.decodeFromString(result)

        return Order(
            id = UUID.fromString(redisModel.id),
            cart = Cart(positions = redisModel.positions.map {
                Position(
                    product = Product(
                        ean = it.ean,
                        price = it.price
                    ), quantity = it.quantity
                )
            }.toMutableList())
        )
    }

    override fun save(order: Order) {
        val redisModel = OrderRedisModel(
            id = order.id.toString(),
            positions = order.cart.positions.map {
                PositionRedisModel(
                    ean = it.product.ean,
                    quantity = it.quantity,
                    price = it.product.price
                )
            }.toMutableList())

        jedis["orders_${order.id}"] = Json.encodeToString(redisModel)
    }
}

