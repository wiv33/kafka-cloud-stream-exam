package com.psawesome.plainproducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Map;
import java.util.Properties;

/**
 * package: com.psawesome.plainproducer
 * author: PS
 * DATE: 2020-11-21 토요일 00:00
 */
@Slf4j
public class ConsumerExam {

    public static void main(String[] args) {
        final Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-java-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        final Map<String, Object> properties = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.GROUP_ID_CONFIG, "test-java-consumer",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.StringSerde.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.StringSerde.class
        );
//
//        var consumer = new KafkaConsumer<>(properties);
//        consumer.subscribe(Collections.singleton("test-java"));
//        Flux.fromStream(Stream.generate(() -> consumer.poll(Duration.ofMillis(500))))
//                .subscribe(System.out::println);

        StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, String> stream = builder.stream("test-java",
                Consumed.with(
                        Serdes.String(), Serdes.String()
                )
        );

        stream.foreach(((key, value) -> log.info("kafka stream key : {}, value : {}", key, value)));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();
    }
}
