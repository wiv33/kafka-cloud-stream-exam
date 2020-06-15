package com.psawesome.kafkacloudstreamexam.books.kstreams;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Properties;

/**
 * package: com.psawesome.kafkacloudstreamexam.books.kstreams
 * author: PS
 * DATE: 2020-06-15 월요일 21:53
 */
@Slf4j
public class KafkaStreamsTellingApp {
  @SneakyThrows
  public static void main(String[] args) {
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "yelling_app_id");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    StreamsBuilder builder = new StreamsBuilder();

    Serde<String> serde = Serdes.String();
    KStream<String, String> firstStream = builder.stream("src-topic", Consumed.with(serde, serde));

    KStream<String, String> toUpperStream = firstStream.mapValues((ValueMapper<String, String>) String::toUpperCase);

    toUpperStream.to("out-topic", Produced.with(serde, serde));

    KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), config);
    kafkaStreams.cleanUp();
    kafkaStreams.start();
    Thread.sleep(35000);
    log.info("shutting down the Telling app now");
    kafkaStreams.close();
  }
}
