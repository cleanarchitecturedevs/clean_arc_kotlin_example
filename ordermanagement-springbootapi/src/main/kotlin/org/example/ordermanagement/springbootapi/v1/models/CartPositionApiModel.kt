package org.example.ordermanagement.springbootapi.v1.models

data class CartPositionApiModel(
    val ean: String,
    val priceTotal: Double,
    val quantity: Int
)

