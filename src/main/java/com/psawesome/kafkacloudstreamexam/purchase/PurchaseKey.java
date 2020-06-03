package com.psawesome.kafkacloudstreamexam.purchase;

import lombok.*;

@Data
@AllArgsConstructor
public class PurchaseKey {
  private String customerId, transactionDate;
}
