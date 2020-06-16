package org.psawesome.streamsInAction.chap3;

import com.psawesome.kafkacloudstreamexam.streamsInAction.serde.StreamsSerdes;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.util.Properties;

@Slf4j
public class ZMartKafkaStreamsApp {

  public static void main(String[] args) {
    getProperties();
    StreamsSerdes.PurchaseSerde();
  }

  private static Properties getProperties() {
    Properties config = new Properties();
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "FirstZmart-Kafka-Streams-Client");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "zmart-purchases");
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "FirstZmart-Kafka-Streams-app");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
    config.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
    return config;
  }
}
