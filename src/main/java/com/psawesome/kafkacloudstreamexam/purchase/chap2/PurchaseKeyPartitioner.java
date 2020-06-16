package com.psawesome.kafkacloudstreamexam.purchase.chap2;

import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;

import java.util.Objects;

public class PurchaseKeyPartitioner extends DefaultPartitioner {
  @Override
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    Object newKey = null;
    if (Objects.nonNull(key)) {
      PurchaseKey purchaseKey = (PurchaseKey) key;
      newKey = purchaseKey.getCustomerId();
      keyBytes = ((String) newKey).getBytes();
    }
    return super.partition(topic, newKey, keyBytes, value, valueBytes, cluster);
  }


}
