package org.example.ordermanagement.usecases.addtocart

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.entities.Product
import org.example.ordermanagement.repositories.OrderRepository
import org.example.ordermanagement.repositories.ProductRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AddToCartUseCaseImplTest {

    @MockK
    private lateinit var presenter: AddToCartPresenter

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var orderRepository: OrderRepository

    private lateinit var addToCart: AddToCartUseCaseImpl

    private val orderId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        addToCart = AddToCartUseCaseImpl(orderRepository, productRepository, presenter)
    }

    @AfterEach
    fun tearDown() = clearAllMocks()

    @Test
    fun `given order not found then should present notFound`() {
        // Given
        val ean = "0012345678905"
        every { orderRepository.byId(orderId) } returns null
        every { presenter.notFound() } returns Unit

        // When
        addToCart.execute(
            AddToCartRequest(
                orderId = orderId,
                ean = ean,
                quantity = 2
            )
        )

        // Then
        verify(exactly = 1) { presenter.notFound() }
    }

    @Test
    fun `given product not found then should present notFound`() {
        // Given
        val ean = "0012345678905"
        every { orderRepository.byId(orderId) } returns Order(id = UUID.randomUUID())
        every { productRepository.productByEan(ean) } returns null
        every { presenter.notFound() } returns Unit

        // When
        addToCart.execute(
            AddToCartRequest(
                orderId = orderId,
                ean = ean,
                quantity = 2
            )
        )

        // Then
        verify(exactly = 1) { presenter.notFound() }
    }

    @Test
    fun `given order could not be saved then should present error`() {
        // Given
        val ean = "0012345678905"
        val order = Order(id = orderId)
        every { orderRepository.byId(orderId) } returns order
        every { orderRepository.save(order) } throws Exception()
        every { productRepository.productByEan(ean) } returns Product(ean = ean, price = 9.99)
        every { presenter.error() } returns Unit

        // When
        addToCart.execute(
            AddToCartRequest(
                orderId = orderId,
                ean = ean,
                quantity = 2
            )
        )

        // Then
        verify(exactly = 1) { presenter.error() }
    }

    @Test
    fun `given order and product found then should present new cart as success`() {
        // Given
        val ean = "0012345678905"
        val order = Order(id = orderId)

        every { orderRepository.byId(orderId) } returns order
        every { orderRepository.save(order) } returns Unit
        every { productRepository.productByEan(ean) } returns Product(ean = ean, price = 9.99)
        every { presenter.success(any()) } returns Unit

        // When
        addToCart.execute(
            AddToCartRequest(
                orderId = orderId,
                ean = ean,
                quantity = 2
            )
        )

        // Then
        val expectedCartDto = listOf(
            CartPositionDto(
                ean = ean,
                quantity = 2,
                priceTotal = 2 * 9.99
            )
        )

        val expectedOrderDto = OrderDto(
            id = orderId,
            totalValue = 2 * 9.99,
            cart = expectedCartDto
        )

        verify(exactly = 1) { presenter.success(AddToCartResponse(order = expectedOrderDto)) }
    }
}
