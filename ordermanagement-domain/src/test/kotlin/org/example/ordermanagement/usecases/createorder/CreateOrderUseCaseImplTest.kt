package org.example.ordermanagement.usecases.createorder

import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.example.ordermanagement.entities.Order
import org.example.ordermanagement.repositories.OrderRepository
import org.example.ordermanagement.util.UUIDGenerator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateOrderUseCaseImplTest {

    @MockK
    private lateinit var presenter: CreateOrderPresenter

    @MockK
    private lateinit var repository: OrderRepository

    private lateinit var usecase: CreateOrderUseCaseImpl

    private val uuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        val uuidGenerator = mockk<UUIDGenerator>()
        every { uuidGenerator.random() } returns uuid

        usecase = CreateOrderUseCaseImpl(
            repository = repository,
            presenter = presenter,
            uuidGenerator = uuidGenerator
        )
    }

    @AfterEach
    fun tearDown() = clearAllMocks()

    @Test
    fun `given order could not be saved then should present error`() {
        // Given
        every { repository.save(any()) } throws Exception("Some DB error")
        every { presenter.error() } returns Unit

        // When
        usecase.execute()

        // Then
        verify(exactly = 1) { presenter.error() }
    }

    @Test
    fun `given order was saved then should present success`() {
        // Given
        val orderSlot = slot<Order>()
        val responseSlot = slot<CreateOrderResponse>()

        every { repository.save(capture(orderSlot)) } returns Unit
        every { presenter.success(capture(responseSlot)) } returns Unit

        // When
        usecase.execute()

        // Then
        verify(exactly = 1) { presenter.success(CreateOrderResponse(orderId = uuid)) }
    }
}


