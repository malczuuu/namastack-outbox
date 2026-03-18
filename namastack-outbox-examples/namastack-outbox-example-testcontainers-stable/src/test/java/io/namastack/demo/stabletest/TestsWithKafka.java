package io.namastack.demo.stabletest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public interface TestsWithKafka {

  @Container
  @ServiceConnection
  @SuppressWarnings("resource")
  KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("apache/kafka:4.2.0"))
          .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
}
