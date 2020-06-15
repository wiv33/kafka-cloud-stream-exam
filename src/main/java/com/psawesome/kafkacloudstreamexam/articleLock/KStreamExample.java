package com.psawesome.kafkacloudstreamexam.articleLock;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class KStreamExample {

  public void startStream() {
    StreamsBuilder builder = new StreamsBuilder();
    // config?
    KStream<String, String> article_lock = builder.stream(
            "article_lock",
            Consumed.with(
                    Serdes.String(),
                    Serdes.String()
            )
    );
    log.info("start streams");
    article_lock.foreach((s, d) -> log.info("key is {}, value is {}", s, d));

  }
}
