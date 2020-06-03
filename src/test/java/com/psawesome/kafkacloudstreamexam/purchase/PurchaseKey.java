package com.psawesome.kafkacloudstreamexam.purchase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseKey {
  private String customerId, transactionDate;
}
