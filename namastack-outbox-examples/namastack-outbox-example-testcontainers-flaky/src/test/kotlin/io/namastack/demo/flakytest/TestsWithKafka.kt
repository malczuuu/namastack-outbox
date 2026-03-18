package io.namastack.demo.flakytest

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

interface TestsWithKafka {

    companion object {
        @Container
        @ServiceConnection
        @JvmField
        val kafkaContainer: KafkaContainer =
            KafkaContainer(DockerImageName.parse("apache/kafka:4.2.0"))
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
    }
}

