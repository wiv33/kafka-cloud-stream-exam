package com.psawesome.kafkacloudstreamexam.streamsInAction.chap2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseKey {
  private String customerId, transactionDate;
}
