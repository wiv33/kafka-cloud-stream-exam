package com.psawesome.kafkacloudstreamexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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

}
