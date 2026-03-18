package io.namastack.demo.flakytest.customer

import io.namastack.demo.flakytest.KafkaTools
import io.namastack.demo.flakytest.TestWithPostgres
import io.namastack.demo.flakytest.TestsWithKafka
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import tools.jackson.databind.json.JsonMapper
import java.time.Duration
import java.util.ArrayList
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await

@ActiveProfiles("context1")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceContext3Tests : TestsWithKafka, TestWithPostgres {

    @Autowired
    private lateinit var customerService: CustomerService

    @Autowired
    private lateinit var connectionDetails: KafkaConnectionDetails

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @Value($$"${demo.topic}")
    private lateinit var demoTopic: String

    private var kafkaConsumer: KafkaConsumer<String, String>? = null

    @BeforeAll
    fun beforeAll() {
        kafkaConsumer = KafkaTools.getTestConsumer(connectionDetails, demoTopic)
    }

    @AfterAll
    fun afterAll() {
        kafkaConsumer?.close()
    }

    @Test
    fun registerCustomerAndVerifyKafkaEvent3() {
        customerService.register("Thomas", "Edison", "thomas.edison@example.com")

        await()
            .atMost(Duration.ofSeconds(5))
            .pollInterval(Duration.ofMillis(200))
            .untilAsserted {
                val messages: ConsumerRecords<String, String> = kafkaConsumer!!.poll(Duration.ofMillis(100))

                assertThat(messages).isNotEmpty

                val records: MutableList<ConsumerRecord<String, String>> = ArrayList()
                messages.forEach { records.add(it) }
                assertThat(records).hasSize(1)

                val event = jsonMapper.readValue(records[0].value(), CustomerRegisteredEvent::class.java)

                assertThat(event.id).isNotNull
                assertThat(event.firstname).isEqualTo("Thomas")
                assertThat(event.lastname).isEqualTo("Edison")
                assertThat(event.email).isEqualTo("thomas.edison@example.com")
            }
    }
}

