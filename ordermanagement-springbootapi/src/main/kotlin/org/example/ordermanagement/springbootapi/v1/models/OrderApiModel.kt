package org.example.ordermanagement.springbootapi.v1.models

data class OrderApiModel(
    val id: String,
    val totalValue: Double,
    val cart: List<CartPositionApiModel>
)
