package io.namastack.demo.stabletest.customer;

import io.namastack.demo.stabletest.TestWithPostgres;
import io.namastack.demo.stabletest.TestsWithKafka;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.namastack.demo.stabletest.KafkaTools.getTestConsumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ActiveProfiles({"context1"})
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceContext1Tests implements TestsWithKafka, TestWithPostgres {

  @Autowired private CustomerService customerService;

  @Autowired private KafkaConnectionDetails connectionDetails;

  @Autowired private JsonMapper jsonMapper;

  @Value("${demo.topic}")
  private String demoTopic;

  private KafkaConsumer<String, String> kafkaConsumer;

  @BeforeAll
  void beforeAll() {
    this.kafkaConsumer = getTestConsumer(connectionDetails, demoTopic);
  }

  @AfterAll
  void afterAll() {
    if (kafkaConsumer != null) {
      kafkaConsumer.close();
    }
  }

  @Test
  void registerCustomerAndVerifyKafkaEvent1() {
    customerService.register("Nicola", "Tesla", "nicola.tesla@example.com");

    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              ConsumerRecords<String, String> messages = kafkaConsumer.poll(Duration.ofMillis(100));

              assertThat(messages).isNotEmpty();

              List<ConsumerRecord<String, String>> records = new ArrayList<>();
              messages.forEach(records::add);
              assertThat(records).hasSize(1);

              CustomerRegisteredEvent event =
                  jsonMapper.readValue(records.get(0).value(), CustomerRegisteredEvent.class);

              assertThat(event.getId()).isNotNull();
              assertThat(event.getFirstname()).isEqualTo("Nicola");
              assertThat(event.getLastname()).isEqualTo("Tesla");
              assertThat(event.getEmail()).isEqualTo("nicola.tesla@example.com");
            });
  }
}
