package org.psawesome.streamsInAction.chap2.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadedConsumerExam {
  private volatile boolean doneConsuming = false;
  private int numberPartitions;
  private ExecutorService executorService;

  public ThreadedConsumerExam(int numberPartitions) {
    this.numberPartitions = numberPartitions;
  }

  public void startConsuming() {
    executorService = Executors.newFixedThreadPool(numberPartitions);
    Properties properties = getConsumerProps();

    for (int i = 0; i < numberPartitions; i++) {
      Runnable consumerThread = getConsumerThread(properties);
      executorService.submit(consumerThread);
    }
  }

  private Runnable getConsumerThread(Properties properties) {
    return () -> {
      try (Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
        consumer.subscribe(Collections.singletonList("article_lock"));
        while (!doneConsuming) {
          ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
          for (ConsumerRecord<String, String> record : records) {
            String message = String.format("Consumed: key = %s value = %s with offset = %d partition = %d",
                    record.key(), record.value(), record.offset(), record.partition());
            log.info(message);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    };
  }

  private Properties getConsumerProps() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", "localhost:9092");
    properties.put("group.id", "simple-consumer-example");
    properties.put("auto.offset.reset", "earliest");
//    properties.put("auto.offset.reset", "latest");
    properties.put("enable.auto.commit", "true");
    properties.put("auto.commit.interval.ms", "3000"); // default 5000
    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    return properties;
  }

  public void stopConsuming() throws InterruptedException {
    doneConsuming = true;
    executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
    executorService.shutdownNow();
  }
}
