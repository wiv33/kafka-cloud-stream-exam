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
    /*
        메시지를 브로커에서 꺼내는 역할
        토픽의 파티션 리더로부터 데이터를 가져옴
        컨슈머의 최대 목적
            -> 컨슈머가 가능한 최대 성능으로 가져오는 것

        push vs pull
        오프셋 순서대로 메시지를 가져온다.

        group coordinator => fetcher, coordinator       => poll     => DB
                                fetch.min.bytes
                                fetch.max.wait.ms
                                enable.auto.commit
                                auto.commit.interval.ms
                                retry.back.off.ms
                                max.poll.records
     */
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
