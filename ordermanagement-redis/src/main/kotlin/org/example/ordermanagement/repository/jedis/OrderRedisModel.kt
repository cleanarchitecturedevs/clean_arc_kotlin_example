package org.example.ordermanagement.repository.jedis

import kotlinx.serialization.Serializable

@Serializable
data class OrderRedisModel(
    val id: String,
    val positions: MutableList<PositionRedisModel>
)
