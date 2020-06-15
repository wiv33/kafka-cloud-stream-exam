package com.psawesome.kafkacloudstreamexam.confluent;

import jdk.jfr.SettingDefinition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.test.TestUtils;
import org.springframework.integration.kafka.dsl.Kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class WordCountExam {
  public static final String INPUT_TOPIC = "streams-plaintext-input";
  public static final String OUTPUT_TOPIC = "streams-wordcount-output";

  public static void main(String[] args) {
    final String BOOTSTRAP_SERVER = "localhost:9092";

    final Properties config = getStreamsConfiguration(BOOTSTRAP_SERVER);

    StreamsBuilder builder = new StreamsBuilder();
    createWordCountStream(builder);

    final KafkaStreams streams = new KafkaStreams(builder.build(), config);

    streams.cleanUp();

    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

  }

  private static void createWordCountStream(final StreamsBuilder builder) {
    KStream<String, String> textLines = builder.stream(INPUT_TOPIC);

    final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

    KTable<String, Long> wordCount = textLines
            .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
            .groupBy(((keyIgnore, word) -> word))
            .count();

    wordCount.toStream().to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
  }


  private static Properties getStreamsConfiguration(final String bootstrap_server) {
    Properties config = new Properties();
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_server);

    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example");
    config.put(StreamsConfig.CLIENT_ID_CONFIG, "wordcount-lambda-example-client");

    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // add library org.apache.kafka:kafka-clients:test:2.3.1
    config.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());

    return config;
  }
}
