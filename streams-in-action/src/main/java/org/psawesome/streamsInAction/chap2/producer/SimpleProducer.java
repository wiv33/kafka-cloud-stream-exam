package org.psawesome.streamsInAction.chap2.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psawesome.kafkacloudstreamexam.streamsInAction.chap2.PurchaseKey;
import org.apache.kafka.clients.producer.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Future;

public class SimpleProducer {
  /*
    ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic some-topic --from-beginning --property "parse.key=true" --property "key.separate=:" --property "print.key=true" --formatter kafka.tools.DefaultMessageFormatter

    kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic some-topic --from-beginning --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

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
      String val = "value";
      ProducerRecord<String, String> record = new ProducerRecord<>("some-topic", new ObjectMapper().writeValueAsString(key), val);
      Callback callback = (metadata, exception) -> Objects.requireNonNull(exception);

      Future<RecordMetadata> send = producer.send(record, callback);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
