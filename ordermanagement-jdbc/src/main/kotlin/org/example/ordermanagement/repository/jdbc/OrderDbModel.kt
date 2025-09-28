package org.example.ordermanagement.repository.jdbc

import java.util.*

data class OrderDbModel(val id: UUID, val positions: List<PositionDbModel>)
