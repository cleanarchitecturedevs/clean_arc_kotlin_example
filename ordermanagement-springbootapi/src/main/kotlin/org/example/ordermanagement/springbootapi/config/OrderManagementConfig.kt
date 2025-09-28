package org.example.ordermanagement.springbootapi.config

import org.example.ordermanagement.repository.jdbc.JdbcOrderRepository
import org.example.ordermanagement.util.UUIDGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class OrderManagementConfig {

    @Bean
    fun uuidGenerator(): UUIDGenerator = UUIDGenerator()

    @Bean
    fun orderRepository(dataSource: DataSource): JdbcOrderRepository = JdbcOrderRepository(dataSource)
}
