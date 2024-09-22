package com.syos.model;

import java.math.BigDecimal;

public class OverTheCounterTransaction extends Transaction {

  public OverTheCounterTransaction(BigDecimal totalAmount) {
    super("over-the-counter", totalAmount);
  }

  // No userId for over-the-counter transactions
  @Override
  public Integer getUserId() {
    return null;
  }
}
