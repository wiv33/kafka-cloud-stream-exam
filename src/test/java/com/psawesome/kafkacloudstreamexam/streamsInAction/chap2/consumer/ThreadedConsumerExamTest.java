package com.psawesome.kafkacloudstreamexam.streamsInAction.chap2.consumer;

import org.junit.jupiter.api.Test;

public class ThreadedConsumerExamTest {

  ThreadedConsumerExam consumerExam;

  @Test
  void testConsumer() throws InterruptedException {
    consumerExam = new ThreadedConsumerExam(3);
    consumerExam.startConsuming();
    Thread.sleep(60000);
    consumerExam.stopConsuming();
  }
}

