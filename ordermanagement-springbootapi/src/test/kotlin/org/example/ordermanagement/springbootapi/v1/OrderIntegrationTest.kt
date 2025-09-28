package org.example.ordermanagement.springbootapi.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.example.ordermanagement.springbootapi.v1.controllers.HttpAddToCartRequest
import org.example.ordermanagement.springbootapi.v1.models.CartPositionApiModel
import org.example.ordermanagement.springbootapi.v1.models.CreateOrderApiModel
import org.example.ordermanagement.springbootapi.v1.models.OrderApiModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@AutoConfigureMockMvc
class OrderIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `when order was created then should be found`() {
        // Arrange & Act
        val result = mockMvc.perform(post("/v1/order"))
            .andExpect(status().isOk)
            .andReturn()

        // Extract the Order ID from the Response
        val objectMapper = ObjectMapper()
        objectMapper.registerKotlinModule()
        val content = result.response.contentAsString
        val createOrderApiModel = objectMapper.readValue<CreateOrderApiModel>(content)

        // Now read the Order
        val httpAddToCartRequest = HttpAddToCartRequest(ean = "012345678912", quantity = 1)
        val addToCartJsonRequest = objectMapper.writeValueAsString(httpAddToCartRequest)
        val addToCartResponse =
            mockMvc.perform(
                post("/v1/order/${createOrderApiModel.id}")
                    .header("Content-Type", "application/json")
                    .content(addToCartJsonRequest))
                .andExpect(status().isOk)
                .andReturn()
        val orderApiModel = objectMapper.readValue<OrderApiModel>(addToCartResponse.response.contentAsString)

        // Then
        val expected = OrderApiModel(
            id = createOrderApiModel.id,
            totalValue = 9.99,
            cart = listOf(
                CartPositionApiModel(ean = "012345678912", priceTotal = 9.99, quantity = 1)
            )
        )
        assertEquals(expected, orderApiModel)
    }
}
