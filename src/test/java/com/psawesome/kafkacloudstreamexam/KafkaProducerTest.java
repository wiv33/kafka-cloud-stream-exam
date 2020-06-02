package com.psawesome.kafkacloudstreamexam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * package: com.psawesome.kafkacloudstreamexam
 * author: PS
 * DATE: 2020-04-12 일요일 15:55
 */
@Slf4j
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
    ProducerRecord<String, String> record = new ProducerRecord<>("article_lock", "lock");
//        ProducerRecord<String, String> record = new ProducerRecord<>("click_log", "1","login");
    producer.send(record);

    producer.close();
  }
// -----------------------------------------

  /*
    하............ 무얼 잘못 생각했지?


   */

  AtomicInteger cnt;
  ExecutorService es;
  List<List<String>> rolesList;
  List<String> userName;

  @BeforeEach
  void setUp() throws Exception {
    cnt = new AtomicInteger(0);
    es = Executors.newFixedThreadPool(20);
    List<String> sa = Arrays.asList("SA", "AT", "AB", "AA");
    List<String> at = Arrays.asList("AT", "BB", "CC", "AB");
    List<String> not = Arrays.asList("TT", "TA");
    rolesList = Arrays.asList(sa, at, not);
    userName = Arrays.asList("PS_awesome_", "SH_Manager_", "JH_Submit_");

  }

  @Test
  void testKafkaSendMessage() throws InterruptedException {
    Properties config = new Properties();
    config.put("bootstrap.servers", "localhost:9092");
//    config.put("group.id", "article_lock");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String, String> producer = new KafkaProducer<>(config);
    while (true) {
      if (cnt.getAndIncrement() % 7 == 0)
        Thread.sleep(3000);
      log.info("# Start submit");
      Future<?> article_lock = es.submit(() -> {
        long artSeq = ThreadLocalRandom.current().nextLong(0, 30);
        ObjectMapper mapper = new ObjectMapper();
        ArticleLock lock = ArticleLock.builder()
                .dateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .state(ThreadLocalRandom.current().nextInt(0, 7) > 3 ? "lock" : "unLock")
                .articleSeq(artSeq)
                .roles(rolesList.get(ThreadLocalRandom.current().nextInt(0, 3)))
                .reporter(userName.get(ThreadLocalRandom.current().nextInt(0, 3)) + Thread.currentThread().getName())
                .build();

        String s = null;

        try {
          s = mapper.writeValueAsString(lock);
          log.info("sent message : {}", s);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
        // 기사 번호 / 날짜 / 기자 / 가진 권한
        ProducerRecord<String, String> record = new ProducerRecord<>("article_lock", s);
        producer.send(record);
        return null;
      });

      if (cnt.get() > 1000) break;
    }

    producer.close();


    es.awaitTermination(1000, TimeUnit.MINUTES);
  }


  @Setter
  @Getter
  @Builder
  static class ArticleLock {
    private long articleSeq;
    private String state;
    private String dateTime;
    private String reporter;
    private List<String> roles;

  }
}
