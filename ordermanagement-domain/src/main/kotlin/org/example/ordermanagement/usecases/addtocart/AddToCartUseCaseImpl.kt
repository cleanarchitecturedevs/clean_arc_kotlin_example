package org.example.ordermanagement.usecases.addtocart

import org.example.ordermanagement.repositories.OrderRepository
import org.example.ordermanagement.repositories.ProductRepository

class AddToCartUseCaseImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val presenter: AddToCartPresenter
) : AddToCartUseCase {

    override fun execute(request: AddToCartRequest) {

        // Load Entities from DB
        val order = orderRepository.byId(request.orderId) ?: return presenter.notFound()
        val product = productRepository.productByEan(request.ean) ?: return presenter.notFound()

        // Execute Business Rules
        order.addToCart(product, request.quantity)

        // Save Entity to DB
        try {
            orderRepository.save(order)
        } catch (ex: Exception) {
            return presenter.error()
        }

        // Present Response

        // Remember: Outside the Application, no one has access to the Entities
        // Map: Domain Entity -> Response DTO
        val response = AddToCartResponse(order = OrderDto(
            id = order.id,
            totalValue = order.totalValue(),
            cart = order.cart.positions.map {
                CartPositionDto(
                    ean = it.product.ean,
                    quantity = it.quantity,
                    priceTotal = it.totalPrice()
                )
            }
        ))

        // Present ResponseDto
        return presenter.success(response)
    }
}
