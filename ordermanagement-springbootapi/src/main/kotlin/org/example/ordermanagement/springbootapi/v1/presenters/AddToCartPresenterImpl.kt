package org.example.ordermanagement.springbootapi.v1.presenters

import org.example.ordermanagement.springbootapi.v1.models.CartPositionApiModel
import org.example.ordermanagement.springbootapi.v1.models.OrderApiModel
import org.example.ordermanagement.usecases.addtocart.AddToCartPresenter
import org.example.ordermanagement.usecases.addtocart.AddToCartResponse
import org.springframework.http.ResponseEntity

class AddToCartPresenterImpl : AddToCartPresenter {

    lateinit var responseEntity: ResponseEntity<Any>

    override fun success(response: AddToCartResponse) {
        val cartApiModel = response.order.cart.map {
            CartPositionApiModel(
                ean = it.ean,
                priceTotal = it.priceTotal,
                quantity = it.quantity
            )
        }

        val orderApiModel = OrderApiModel(
            id = response.order.id.toString(),
            totalValue = response.order.totalValue,
            cart = cartApiModel
        )

        responseEntity = ResponseEntity.ok(orderApiModel)
    }

    override fun notFound() {
        responseEntity = ResponseEntity.notFound().build()
    }

    override fun error() {
        responseEntity = ResponseEntity.internalServerError().build()
    }
}
