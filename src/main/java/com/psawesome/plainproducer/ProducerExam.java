package com.psawesome.plainproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * package: com.psawesome.plainproducer
 * author: PS
 * DATE: 2020-11-15 일요일 23:47
 */
@Slf4j
public class ProducerExam {
  static final String PUBLIC_IP = "localhost";

  public static void main(String[] args) {
    var mapper = new ObjectMapper();
    var message = Map.of(
            "id", UUID.randomUUID().toString(),
            "name", "strings");

    final String bootstrapServers = IntStream.range(9092, 9093)
            .mapToObj(i -> String.format("%s:%d", PUBLIC_IP, i))
            .collect(Collectors.joining(","));
    System.out.println("bootstrapServers = " + bootstrapServers);
    final Map<String, Object> properties =
            Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.CLIENT_ID_CONFIG, "sender-test-java",
//                    ProducerConfig.ACKS_CONFIG, "all",
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

    Callback callback = ((metadata, exception) -> {
      if (Objects.nonNull(exception)) {
        log.error("encountered exception : {}", exception.getMessage());
      }
      log.info("metadata is {}", metadata.toString());
    });

    try {
      final ProducerRecord<String, String> record = new ProducerRecord<>("test-java",
              "word2vec-nlp-tutorial",
              mapper.writeValueAsString(message));
      Future<RecordMetadata> send = producer.send(record, callback);
//      producer.commitTransaction();
      final RecordMetadata recordMetadata = send.get();
      System.out.println(recordMetadata.toString());
    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    System.out.println("close");
    producer.close();

  }
}
