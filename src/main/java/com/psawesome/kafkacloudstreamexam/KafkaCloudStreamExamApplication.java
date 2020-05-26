package com.psawesome.kafkacloudstreamexam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.internals.QueryableStoreProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableBinding(AnalyticsBinding.class)
public class KafkaCloudStreamExamApplication {


    /*
        log.retention.hours:
            168H => 72H

        delete.topic.enable:
            false => true
            disc full 일 경우 급하게라도 삭제할 수 있도록 설정하는 것
        allow.auto.create.topics:
            enable => disable

        log.dirs:
            c:/Tmp or /tmp and /var/tmp
            OS에서 관리하는 작업에 의해 삭제될 수 있다.
            로그 파일은 반드시 별도의 디렉토리를 사용하는 것이 유용할 수 있다.

     */

  public static void main(String[] args) {
    SpringApplication.run(KafkaCloudStreamExamApplication.class, args);
  }


  @Component
  @RequiredArgsConstructor
  public static class PageViewEventSource implements ApplicationRunner {

    private final MessageChannel pageViewOut;

    @Override
    public void run(ApplicationArguments args) throws Exception {
      List<String> names = Arrays.asList("mfisher", "dyser", "schacko", "abinlan", "ozhurakousky", "grussell");
      List<String> pages = Arrays.asList("blong", "sitemap", "initializr", "news", "colophon", "about");

      Runnable runnable = () -> {
        String rPage = pages.get(ThreadLocalRandom.current().nextInt(pages.size()));
        String rName = pages.get(ThreadLocalRandom.current().nextInt(names.size()));

        PageViewEvent pageViewEvent = new PageViewEvent(rName, rPage, Math.random() > .5 ? 10 : 1000);
        Message<PageViewEvent> message = MessageBuilder
                .withPayload(pageViewEvent)
                .setHeader(KafkaHeaders.MESSAGE_KEY, pageViewEvent.getUserId().getBytes())
                .build();

        try {
          this.pageViewOut.send(message);
          log.info("sent {}", message.toString());
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      };
      Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
    }
  }
/*

  @Slf4j
  @Component
  public static class PageViewEventSink {

    @StreamListener
    @SendTo(AnalyticsBinding.PAGE_COUNT_OUT)
    public KStream<String, Long> process(@Input(AnalyticsBinding.PAGE_VIEWS_IN) KStream<String, PageViewEvent> events) {

      KTable<String, Long> kTable = events.filter(((key, value) -> value.getDuration() > 10))
              .map((key, value) -> new KeyValue<>(value.getPage(), "0"))
              .groupByKey()
//              .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
              .count(Materialized.as(AnalyticsBinding.PAGE_VIEW_MV));

      events.leftJoin(kTable, new ValueJoiner<PageViewEvent, Long, LocalDateTime>() {
        @Override
        public LocalDateTime apply(PageViewEvent value1, Long value2) {
          return null;
        }
      });
    }
      return events
              .filter((key, value) -> value.getDuration() > 10)
              .map((key, value) -> new KeyValue<>(value.getPage(), "0"))
              .groupByKey()
              .count(Materialized.as(AnalyticsBinding.PAGE_COUNT_MV))
              .toStream();

    }
*/

  @Slf4j
  @Component
  public static class PageViewEventProcessor {

    @StreamListener
    @SendTo(AnalyticsBinding.PAGE_COUNT_OUT)
    public KStream<String, Long> process(@Input(AnalyticsBinding.PAGE_VIEWS_IN) KStream<String, PageViewEvent> events) {
      return events
              .filter((key, value) -> value.getDuration() > 10)
              .map((key, value) -> new KeyValue<>(value.getPage(), "0"))
              .groupByKey()
              .count(Materialized.as(AnalyticsBinding.PAGE_COUNT_MV))
              .toStream();
    }
  }


  @Slf4j
  @Component
  public static class PageCountSink {
    @StreamListener
    public void process(@Input(AnalyticsBinding.PAGE_COUNT_IN) KTable<String, Long> counts) {
      counts.toStream()
              .foreach((key, value) -> log.info("{} = {}", key, value));
    }
  }
/*

  @RestController
  @RequiredArgsConstructor
  public static class CountRestController {

    private final QueryableStoreProvider registry;

    @GetMapping("/counts")
    public Map<String, Long> counts() {
      Map<String, Long> counts = new HashMap<>();
      ReadOnlyKeyValueStore<String, Long> store = this.registry.getStore(AnalyticsBinding.PAGE_COUNT_MV, QueryableStoreTypes.keyValueStore());
      KeyValueIterator<String, Long> all = store.all();
      while (all.hasNext()) {
        KeyValue<String, Long> value = all.next();
        counts.put(value.key, value.value);
      }
      return counts;
    }
  }
*/

}


interface AnalyticsBinding {
  String PAGE_VIEW_OUT = "pvout";
  String PAGE_VIEWS_IN = "pvin";
  String PAGE_COUNT_MV = "pcmv";
  String PAGE_COUNT_OUT = "pcout";
  String PAGE_COUNT_IN = "pcin";

  @Output
  MessageChannel pageViewOut();

  @Input(PAGE_VIEWS_IN)
  KStream<String, PageViewEvent> pageViewsIn();

  @Output(PAGE_COUNT_OUT)
  KStream<String, Long> pageCountOut();

  @Input(PAGE_COUNT_IN)
  KTable<String, Long> pageCountIn();
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class PageViewEvent {
  private String userId, page;
  private long duration;
}