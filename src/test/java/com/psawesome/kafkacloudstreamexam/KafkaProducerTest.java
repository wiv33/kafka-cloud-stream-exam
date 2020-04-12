package com.psawesome.kafkacloudstreamexam;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * package: com.psawesome.kafkacloudstreamexam
 * author: PS
 * DATE: 2020-04-12 일요일 15:55
 */
public class KafkaProducerTest {

    @Test
    void testSendMessage() {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092"); // 2개 이상의 서버 구축
        // 키는 메시지 전송 시 토픽의 타입 지정에 사용된다.
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(config);
        ProducerRecord<String, String> record = new ProducerRecord<>("click_log", "login");
//        ProducerRecord<String, String> record = new ProducerRecord<>("click_log", "1","login");
        producer.send(record);

        producer.close();
    }
}
