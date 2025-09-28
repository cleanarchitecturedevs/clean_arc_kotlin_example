package org.example.ordermanagement.usecases.createorder

interface CreateOrderPresenter {
    fun success(response: CreateOrderResponse)
    fun error()
}
