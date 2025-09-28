package org.example.ordermanagement.repository.jedis

import kotlinx.serialization.Serializable

@Serializable
data class PositionRedisModel(
    val ean: String,
    val quantity: Int,
    val price: Double,
)
