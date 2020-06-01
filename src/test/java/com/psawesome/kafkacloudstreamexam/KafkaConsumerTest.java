package com.psawesome.kafkacloudstreamexam;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * package: com.psawesome.kafkacloudstreamexam
 * author: PS
 * DATE: 2020-04-14 화요일 21:15
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("순서가 있는 컨슈머 활동")
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

  Properties config;

  @BeforeEach
  void beforeEach() {
    config = getConfig();
  }

  @Test
  @Order(0)
  void testConsumer() {

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);

    consumer.subscribe(Collections.singletonList("pageViewOut"));


    Stream.generate(() -> consumer.poll(Duration.ofMillis(500)))
            .limit(10)
            .forEach(s ->
                    s.forEach(v ->
                            System.out.println("record = " + v.value())));

        /*while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("record = " + record.value());
            }
        }*/
  }


  @Test
  @Order(1)
  @DisplayName("일반적인 컨슈머")
  void testConsumerOne() {
    System.out.println("KafkaConsumerTest.testConsumerOne");
  }

  @Test
  @Order(2)
  @DisplayName("일반적인 컨슈머 -> delay 전")
  void testConsumerFast() {
    System.out.println("KafkaConsumerTest.testConsumerFast");
  }

  @Test
  @Order(3)
  @DisplayName("네트워크 지연 컨슈머")
  void testConsumer_Delay() {
    System.out.println("KafkaConsumerTest.testConsumer_Delay");
  }

  @Test
  @Order(4)
  @DisplayName("지연이 요청한 후 호출")
  void testConsumerFastTwo() {
    System.out.println("KafkaConsumerTest.testConsumerFastTwo");
  }

  private Properties getConfig() {
    Properties config = new Properties();
    config.put("bootstrap.servers", "localhost:9092");
    config.put("group.id", "article_lock_group");
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    return config;
  }
}
