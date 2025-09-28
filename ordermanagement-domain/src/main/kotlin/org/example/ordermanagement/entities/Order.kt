package org.example.ordermanagement.entities

import java.util.UUID

class Order(var id: UUID, val cart: Cart = Cart(positions = mutableListOf())) {

    fun addToCart(product: Product, quantity: Int) {
        cart.addToCart(product, quantity)
    }

    fun totalValue(): Double = cart.positions.sumOf { it.totalPrice() }
}

class Cart(val positions: MutableList<Position>) {
    fun addToCart(product: Product, quantity: Int) {
        positions.addLast(Position(product, quantity))
    }
}

class Position(val product: Product, val quantity: Int) {
    fun totalPrice() = quantity * product.price
}
