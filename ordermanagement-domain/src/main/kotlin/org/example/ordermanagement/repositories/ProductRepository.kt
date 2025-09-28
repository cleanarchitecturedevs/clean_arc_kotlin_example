package org.example.ordermanagement.repositories

import org.example.ordermanagement.entities.Product

interface ProductRepository {
    fun productByEan(ean: String): Product?
}
