package io.namastack.demo.flakytest

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails
import org.junit.jupiter.api.Assertions.fail
import java.time.Duration
import java.util.Collections
import java.util.HashMap
import java.util.UUID

object KafkaTools {

    fun getTestConsumer(connectionDetails: KafkaConnectionDetails, topic: String): KafkaConsumer<String, String> {
        val bootstrapServers = connectionDetails.consumer.bootstrapServers.joinToString(",")

        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test-consumer.$topic.${UUID.randomUUID()}"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        val consumer = KafkaConsumer(props, StringDeserializer(), StringDeserializer())
        consumer.subscribe(Collections.singletonList(topic))

        var i = 100
        while (--i > 0) {
            consumer.poll(Duration.ofMillis(100))
            if (consumer.assignment().isNotEmpty()) {
                break
            }
        }

        if (consumer.assignment().isEmpty()) {
            fail<Any>("KafkaConsumer was not assigned to any partitions")
        }
        return consumer
    }
}

