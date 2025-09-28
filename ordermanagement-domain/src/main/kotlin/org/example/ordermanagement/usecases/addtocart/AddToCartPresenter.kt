package org.example.ordermanagement.usecases.addtocart

interface AddToCartPresenter {
    fun success(response: AddToCartResponse)
    fun notFound()
    fun error()
}
