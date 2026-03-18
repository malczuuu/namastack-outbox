package io.namastack.demo.flakytest.customer

import java.util.UUID

data class CustomerRegisteredEvent(
    val id: UUID,
    val firstname: String,
    val lastname: String,
    val email: String,
)
