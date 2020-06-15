package com.psawesome.kafkacloudstreamexam.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

public class KStreamExam {
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
