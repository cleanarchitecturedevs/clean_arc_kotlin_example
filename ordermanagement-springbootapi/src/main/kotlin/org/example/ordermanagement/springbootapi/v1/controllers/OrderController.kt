package org.example.ordermanagement.springbootapi.v1.controllers

import org.example.ordermanagement.entities.Product
import org.example.ordermanagement.repositories.ProductRepository
import org.example.ordermanagement.repository.jdbc.JdbcOrderRepository
import org.example.ordermanagement.springbootapi.v1.presenters.AddToCartPresenterImpl
import org.example.ordermanagement.springbootapi.v1.presenters.CreateOrderPresenterImpl
import org.example.ordermanagement.usecases.addtocart.AddToCartRequest
import org.example.ordermanagement.usecases.addtocart.AddToCartUseCaseImpl
import org.example.ordermanagement.usecases.createorder.CreateOrderUseCaseImpl
import org.example.ordermanagement.util.UUIDGenerator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/order")
class OrderController(
    private val orderRepository: JdbcOrderRepository,
    private val uuidGenerator: UUIDGenerator
) {
    @PostMapping
    fun create(): ResponseEntity<Any> {
        val presenter = CreateOrderPresenterImpl()
        val usecase = CreateOrderUseCaseImpl(
            presenter = presenter,
            repository = orderRepository,
            uuidGenerator = uuidGenerator
        )
        usecase.execute()
        return presenter.responseEntity
    }

    @PostMapping("/{orderId}")
    fun addToCart(
        @PathVariable orderId: String,
        @RequestBody request: HttpAddToCartRequest
    ): ResponseEntity<Any> {
        val presenter = AddToCartPresenterImpl()
        val usecase = AddToCartUseCaseImpl(
            presenter = presenter,
            orderRepository = orderRepository,
            productRepository = ProductRepositoryImpl()
        )
        usecase.execute(
            AddToCartRequest(
                orderId = UUID.fromString(orderId),
                ean = request.ean,
                quantity = request.quantity
            )
        )
        return presenter.responseEntity
    }
}

class ProductRepositoryImpl : ProductRepository {
    override fun productByEan(ean: String): Product? {
        return Product(ean = ean, price = 9.99)
    }
}
