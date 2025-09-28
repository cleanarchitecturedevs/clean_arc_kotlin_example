package org.example.ordermanagement.repository.jdbc

import kotlinx.serialization.Serializable

/**
 * Database Models are data classes that represent the structure of the Database
 * They have no business rules and are not coupled to anything but the DB structure.
 *
 * This allows you to change your Domain Model (Entities) without changing your Database.
 */
@Serializable
data class PositionDbModel(
    val ean: String,
    val quantity: Int,
    val price: Double,
)
