package org.example.ordermanagement.springbootapi.v1

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod.WHEN_AVAILABLE
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(useMainMethod = WHEN_AVAILABLE)
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class
)
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {
    companion object {
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("order_management")
            withUsername("writer")
            withPassword("fa8s7ehr8a97dfh8")
            withExposedPorts(5432)
            withInitScript("db/init.sql")
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            postgresContainer.start()

            TestPropertyValues.of(
                "spring.datasource.url=jdbc:postgresql://localhost:" + postgresContainer.firstMappedPort + "/" + postgresContainer.databaseName,
                "spring.datasource.username=" + postgresContainer.username,
                "spring.datasource.password=" + postgresContainer.password
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
