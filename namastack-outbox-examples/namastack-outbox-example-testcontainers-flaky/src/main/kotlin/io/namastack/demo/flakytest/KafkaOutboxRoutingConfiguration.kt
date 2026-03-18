package io.namastack.demo.flakytest

import io.namastack.demo.flakytest.customer.CustomerRegisteredEvent
import io.namastack.outbox.kafka.KafkaOutboxRouting
import io.namastack.outbox.kafka.kafkaOutboxRouting
import io.namastack.outbox.routing.selector.OutboxPayloadSelector.Companion.type
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaOutboxRoutingConfiguration {

    @Bean
    fun kafkaRouting(@Value($$"${demo.topic}") demoTopic: String): KafkaOutboxRouting =
        kafkaOutboxRouting {
            route(type(CustomerRegisteredEvent::class.java)) {
                target(demoTopic)
                key { _, metadata -> metadata.key }
                headers { payload, _ ->
                    mapOf(
                        "CustomerMail" to (payload as CustomerRegisteredEvent).email,
                    )
                }
            }
            defaults {
                target("default-topic")
                key { _, metadata -> metadata.key }
                headers { payload, _ -> mapOf("eventType" to payload.javaClass.simpleName) }
            }
        }
}
