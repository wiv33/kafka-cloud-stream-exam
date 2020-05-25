package com.psawesome.kafkacloudstreamexam;

import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
                    log.info("sent {}" , message.toString());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            };
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
        }
    }

}


interface AnalyticsBinding {
    String PAGE_VIEW_OUT = "pvout";

    @Output
    MessageChannel pageViewOut();
}


@Data
@AllArgsConstructor @NoArgsConstructor
class PageViewEvent {
    private String userId, page;
    private long duration;
}