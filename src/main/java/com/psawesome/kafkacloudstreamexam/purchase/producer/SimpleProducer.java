package com.psawesome.kafkacloudstreamexam.purchase.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psawesome.kafkacloudstreamexam.purchase.PurchaseKey;
import org.apache.kafka.clients.producer.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Future;

public class SimpleProducer {
/*
  ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic some-topic --from-beginning --property "parse.key=true" --property "key.separate=:" --property "print.key=true" --formatter kafka.tools.DefaultMessageFormatter

 */
  public static void main(String[] args) {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("acks", "all");
    properties.put("retries", "3");
//    properties.put("compression.type", "snappy");
    //This line in for demonstration purposes
//    properties.put("partitioner.class", PurchaseKeyPartitioner.class.getName());

    PurchaseKey key = new PurchaseKey("1234", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
      // ProducerRecord value 는
      /*
        state: String
        modified state by user: String
        update time: String
        변경 당한 사람: String (nullable)
       */
      String val = "value";
      ProducerRecord<String, String> record = new ProducerRecord<>("some-topic", new ObjectMapper().writeValueAsString(key), val);
      Callback callback = (metadata, exception) -> Objects.requireNonNull(exception);

      Future<RecordMetadata> send = producer.send(record, callback);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
