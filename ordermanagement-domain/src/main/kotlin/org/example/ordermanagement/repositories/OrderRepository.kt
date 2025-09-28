package org.example.ordermanagement.repositories

import org.example.ordermanagement.entities.Order
import java.util.UUID

interface OrderRepository {
    fun byId(id: UUID): Order?
    fun save(order: Order)
}
