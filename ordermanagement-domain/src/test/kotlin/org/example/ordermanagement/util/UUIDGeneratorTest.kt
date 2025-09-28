package org.example.ordermanagement.util

import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class UUIDGeneratorTest {

    @Test
    fun `generated random uuids`() {
        // Given
        val generator = UUIDGenerator()

        // When
        val uuid1 = generator.random()
        val uuid2 = generator.random()

        // Then
        assertNotEquals(uuid1, uuid2)
    }
}
