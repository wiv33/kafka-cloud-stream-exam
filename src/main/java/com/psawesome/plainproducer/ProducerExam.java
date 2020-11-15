package com.psawesome.plainproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * package: com.psawesome.plainproducer
 * author: PS
 * DATE: 2020-11-15 일요일 23:47
 */
@Slf4j
public class ProducerExam {
    public static void main(String[] args) {

        var mapper = new ObjectMapper();
        var message = Map.of(
                "id", "1",
                "name", "strings");

        var bootstrapServers = "localhost:9092";
        final Map<String, Object> properties =
                Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ProducerConfig.CLIENT_ID_CONFIG, "sender-test-java",
//                        ProducerConfig.ACKS_CONFIG, "all",
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
            final ProducerRecord<String, String> record = new ProducerRecord<>("word2vec-nlp-tutorial",
                    mapper.writeValueAsString(message));
            Future<RecordMetadata> send = producer.send(record, callback);
            producer.flush();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}
