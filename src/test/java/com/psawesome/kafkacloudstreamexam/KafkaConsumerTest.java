package com.psawesome.kafkacloudstreamexam;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * package: com.psawesome.kafkacloudstreamexam
 * author: PS
 * DATE: 2020-04-14 화요일 21:15
 */
public class KafkaConsumerTest {

    @Test
    void testConsumer() {

        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("group.id", "click_log_group");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);

        consumer.subscribe(Arrays.asList("click_log"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("record = " + record.value());
            }
        }
    }
}
