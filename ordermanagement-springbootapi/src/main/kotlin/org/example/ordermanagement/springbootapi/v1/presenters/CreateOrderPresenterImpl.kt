package org.example.ordermanagement.springbootapi.v1.presenters

import org.example.ordermanagement.springbootapi.v1.models.CreateOrderApiModel
import org.example.ordermanagement.springbootapi.v1.models.ErrorApiModel
import org.example.ordermanagement.usecases.createorder.CreateOrderPresenter
import org.example.ordermanagement.usecases.createorder.CreateOrderResponse
import org.springframework.http.ResponseEntity

class CreateOrderPresenterImpl : CreateOrderPresenter {

    lateinit var responseEntity: ResponseEntity<Any>

    override fun success(response: CreateOrderResponse) {
        val viewModel = CreateOrderApiModel(id = response.orderId.toString())
        responseEntity = ResponseEntity.ok(viewModel)
    }

    override fun error() {
        val viewModel = ErrorApiModel(message = "could not create order")
        responseEntity = ResponseEntity.internalServerError().body(viewModel)
    }
}
