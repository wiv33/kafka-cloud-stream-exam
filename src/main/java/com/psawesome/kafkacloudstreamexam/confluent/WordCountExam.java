package com.psawesome.kafkacloudstreamexam.confluent;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.test.TestUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * 2) Create the input and output topics used by this example.
 * <pre>
 * {@code
 * $ bin/kafka-topics --create --topic streams-plaintext-input \
 *                    --zookeeper localhost:2181 --partitions 1 --replication-factor 1
 * $ bin/kafka-topics --create --topic streams-wordcount-output \
 *                    --zookeeper localhost:2181 --partitions 1 --replication-factor 1
 * }</pre>
 * <p>
 * $ bin/kafka-console-producer --broker-list localhost:9092 --topic streams-plaintext-input
 */

public class WordCountExam {
  public static final String INPUT_TOPIC = "streams-plaintext-input";
  public static final String OUTPUT_TOPIC = "streams-wordcount-output";
  static final String BOOTSTRAP_SERVERS;

  static {
    final String publicIP = "52.79.184.210";
    BOOTSTRAP_SERVERS = String.format("%s:9092,%s:9093,%s:9094", publicIP, publicIP, publicIP) ;
  }

  public static void main(String[] args) {

    generateWord();

    final Properties config = getStreamsConfiguration(BOOTSTRAP_SERVERS);

    StreamsBuilder builder = new StreamsBuilder();
    createWordCountStream(builder);

    final KafkaStreams streams = new KafkaStreams(builder.build(), config);

    streams.cleanUp();

    streams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

  }

  private static void generateWord() {
    /*
       hello kafka streams<ENTER>
       all streams lead to kafka<ENTER>
       join kafka summit<ENTER>
    */

    /*
      run command
      kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-wordcount-output --from-beginning --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
    */

    try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerConfig())) {
      Arrays.asList("hello kafka streams", "all streams lead to kafka", "join kafka summit")
              .forEach(v -> {
                ProducerRecord<String, String> record = new ProducerRecord<>("streams-plaintext-input", v);
                Callback callback = (metadata, exception) -> Objects.requireNonNull(exception);
                Future<RecordMetadata> send = producer.send(record, callback);
              });
    }
  }

  private static Properties producerConfig() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", BOOTSTRAP_SERVERS);
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("acks", "all");
    properties.put("retries", "3");
    return properties;
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


  private static Properties getStreamsConfiguration(final String bootstrap_servers) {
    Properties config = new Properties();
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);

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
