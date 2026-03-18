package io.namastack.demo.flakytest

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.postgresql.PostgreSQLContainer

interface TestWithPostgres {

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val postgresContainer: PostgreSQLContainer = PostgreSQLContainer("postgres:18.3-alpine")
    }
}

