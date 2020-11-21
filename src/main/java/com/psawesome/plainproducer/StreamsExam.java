package com.psawesome.plainproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

/**
 * package: com.psawesome.plainproducer
 * author: PS
 * DATE: 2020-11-21 토요일 21:12
 */
@Slf4j
public class StreamsExam {
  public static void main(String[] args) {
    final Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-java-app");
    final String IP = "localhost";
    final String bootstrapServers = String.format("%s:9092", IP);
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    StreamsBuilder builder = new StreamsBuilder();
    final Serde<String> serde = Serdes.String();
    final KStream<String, String> stream = builder.stream("test-java", Consumed.with(serde, serde));
    stream.flatMapValues(value ->
            /*
             * TODO 무엇을 **생산**할 것인가? compile error
             * */
//            Arrays.asList(value.split("-"))
    ).to("test-python", Produced.with(serde, serde));

    stream.foreach(((key, value) -> log.info("kafka stream key : {}, value : {}", key, value)));

    KafkaStreams streams = new KafkaStreams(builder.build(), config);
    streams.cleanUp();
    streams.start();
    streams.allMetadata()
            .forEach(System.out::println);

  }


}
