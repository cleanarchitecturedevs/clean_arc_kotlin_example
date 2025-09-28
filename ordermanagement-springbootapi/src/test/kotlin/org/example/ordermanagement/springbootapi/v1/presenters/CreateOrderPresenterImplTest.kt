package org.example.ordermanagement.springbootapi.v1.presenters

import org.example.ordermanagement.springbootapi.v1.models.CreateOrderApiModel
import org.example.ordermanagement.usecases.createorder.CreateOrderResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class CreateOrderPresenterImplTest {

    @Test
    fun `error presents 500`() {
        // Given
        val presenter = CreateOrderPresenterImpl()

        // When
        presenter.error()

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, presenter.responseEntity.statusCode)
    }

    @Test
    fun `success presents 200 with view model`() {
        // Given
        val presenter = CreateOrderPresenterImpl()
        val uuid = UUID.randomUUID()

        // When
        presenter.success(CreateOrderResponse(uuid))

        // Then
        assertEquals(HttpStatus.OK, presenter.responseEntity.statusCode)
        assertEquals(CreateOrderApiModel(id = uuid.toString()), presenter.responseEntity.body)
    }
}
