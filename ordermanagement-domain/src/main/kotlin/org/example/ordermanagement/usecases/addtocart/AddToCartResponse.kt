package org.example.ordermanagement.usecases.addtocart

import java.util.UUID

data class AddToCartResponse(
    val order: OrderDto
)

data class OrderDto(
    val id: UUID,
    val totalValue: Double,
    val cart: List<CartPositionDto>
)

data class CartPositionDto(
    val ean: String,
    val quantity: Int,
    val priceTotal: Double
)
