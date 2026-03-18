package io.namastack.demo.stabletest;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

public class KafkaTools {

  public static KafkaConsumer<String, String> getTestConsumer(
      KafkaConnectionDetails connectionDetails, String topic) {
    String bootstrapServers =
        String.join(",", connectionDetails.getConsumer().getBootstrapServers());

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer." + topic + "." + UUID.randomUUID());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    KafkaConsumer<String, String> consumer =
        new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());

    consumer.subscribe(Collections.singletonList(topic));

    int i = 100;

    while (--i > 0) {
      consumer.poll(Duration.ofMillis(100));
      if (!consumer.assignment().isEmpty()) {
        break;
      }
    }

    if (consumer.assignment().isEmpty()) {
      fail("KafkaConsumer was not assigned to any partitions");
    }
    return consumer;
  }
}
