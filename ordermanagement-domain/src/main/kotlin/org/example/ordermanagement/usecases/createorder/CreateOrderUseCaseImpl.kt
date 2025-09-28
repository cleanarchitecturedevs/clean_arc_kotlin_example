package org.example.ordermanagement.usecases.createorder

import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.repositories.OrderRepository
import org.example.ordermanagement.util.UUIDGenerator
import java.util.logging.Logger

class CreateOrderUseCaseImpl(
    private val repository: OrderRepository,
    private val presenter: CreateOrderPresenter,
    private val uuidGenerator: UUIDGenerator
) : CreateOrderUseCase {

    private val logger = Logger.getLogger(CreateOrderUseCaseImpl::class.java.toString())

    override fun execute() {
        val order = Order(id = uuidGenerator.random())

        try {
            repository.save(order)
        } catch (ex: Exception) {
            logger.info("Could not save Order: ${ex.message}")
            return presenter.error()
        }

        return presenter.success(CreateOrderResponse(orderId = order.id))
    }
}

