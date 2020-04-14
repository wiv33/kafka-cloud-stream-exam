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
    /*
        Ack = 0 빠른 전송, 메시지 손실 가능성 있음 (0.29ms)
        Ack = 1 빠른 전송, 메시지 손실 가능성 있음 (1.05ms)
        Ack = All 느린 전송, 메시지 손실 없음 (2.05ms)

        buffer.memory 안에 배치 형태로 모아둔다.
        linger.ms 인 지연시간만큼 보관 후 보낸다.

        Throughput
            batch.size -> increase
            linger.ms -> increase
            compression.type -> snappy.lz4
            acks -> 1
        Latency
            linger.ms -> 0
            compression.type -> none
            acks -> 0
        Durability
            batch.size -> increase
            linger.ms -> increase
            acks -> all
     */
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
