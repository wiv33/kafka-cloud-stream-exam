package com.psawesome.kafkacloudstreamexam.articleLock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KStreamExampleTest {


  @Test
  void testStartKStream() throws InterruptedException {
    KStreamExample kStreamExample = new KStreamExample();
    kStreamExample.startStream();

    Thread.sleep(60000);
  }
}