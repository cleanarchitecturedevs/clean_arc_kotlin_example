package org.example.ordermanagement.springbootapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderManagementApiApplication

fun main(args: Array<String>) {
    runApplication<OrderManagementApiApplication>(*args)
}
