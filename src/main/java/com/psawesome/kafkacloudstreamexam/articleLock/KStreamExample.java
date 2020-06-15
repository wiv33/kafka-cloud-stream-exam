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

  public void globalKTableRun() {
    StreamsBuilder builder = new StreamsBuilder();
    GlobalKTable<String, Long> wordCounts = builder.globalTable(
            "word-counts-input-topic",
            Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(
                    "word-counts-global-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
    );
  }

  public void kStreamToKStreamArray_branch() {
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, Long> stream = builder.stream("word-counts",
            Consumed.with(
                    Serdes.String(),
                    Serdes.Long()
            )
    );

    KStream<String, Long>[] branch = stream.branch(
            (key, value) -> key.startsWith("A"),
            (key, value) -> key.startsWith("B"),
            (key, value) -> value > 3
    );
    for (KStream<String, Long> stringLongKStream : branch) {

    }
  }
}
