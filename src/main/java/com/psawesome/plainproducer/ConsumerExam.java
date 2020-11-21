package com.psawesome.plainproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * package: com.psawesome.plainproducer
 * author: PS
 * DATE: 2020-11-21 토요일 00:00
 */
@Slf4j
public class ConsumerExam {

  public static void main(String[] args) {
    final String IP = "localhost";
    final String bootstrapServers = String.format("%s:9092", IP);

    final Map<String, Object> properties = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG, "test-java-consumer",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
    );

    var consumer = new KafkaConsumer<>(properties);
    consumer.subscribe(Collections.singleton("test-java"));
    Flux.fromStream(Stream.generate(() -> consumer.poll(Duration.ofSeconds(3))))
            .delayElements(Duration.ofSeconds(3))
            .flatMap(Flux::fromIterable)
            /*
            TODO 무엇을 추출할 것인가?

            */
            .subscribe(System.out::println);

  }
}
