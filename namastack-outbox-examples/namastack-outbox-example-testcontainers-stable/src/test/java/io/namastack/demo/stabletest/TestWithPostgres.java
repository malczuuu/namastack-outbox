package io.namastack.demo.stabletest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

public interface TestWithPostgres {

  @Container
  @ServiceConnection
  @SuppressWarnings("resource")
  PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:18.3-alpine");
}
