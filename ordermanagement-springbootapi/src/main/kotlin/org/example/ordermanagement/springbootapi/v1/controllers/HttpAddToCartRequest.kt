package org.example.ordermanagement.springbootapi.v1.controllers

data class HttpAddToCartRequest(
    val ean: String,
    val quantity: Int
)
