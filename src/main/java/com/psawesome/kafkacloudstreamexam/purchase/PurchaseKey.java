package com.psawesome.kafkacloudstreamexam.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseKey {
  private String customerId, transactionDate;
}
