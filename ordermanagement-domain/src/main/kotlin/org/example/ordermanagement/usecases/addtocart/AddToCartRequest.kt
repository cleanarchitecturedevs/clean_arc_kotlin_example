package org.example.ordermanagement.usecases.addtocart

import jakarta.validation.constraints.Size
import java.util.*

data class AddToCartRequest(
    val orderId: UUID,

    @field:Size(min = 13, max = 13)
    val ean: String,

    val quantity: Int
)

