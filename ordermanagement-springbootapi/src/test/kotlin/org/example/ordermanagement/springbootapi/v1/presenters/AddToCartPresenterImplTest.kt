package org.example.ordermanagement.springbootapi.v1.presenters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class AddToCartPresenterImplTest {

    @Test
    fun `present not found should create 404 ResponseEntity`() {
        // Given
        val presenter = AddToCartPresenterImpl()

        // When
        presenter.notFound()

        // Then
        assertEquals(404, presenter.responseEntity.statusCode.value())
    }

    @Test
    fun `present internal error should create 500 ResponseEntity`() {
        // Given
        val presenter = AddToCartPresenterImpl()

        // When
        presenter.error()

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, presenter.responseEntity.statusCode)
    }
}
