package com.syos.model;

import com.syos.enums.TransactionType;

import java.math.BigDecimal;

public class OverTheCounterTransaction extends Transaction {

  public OverTheCounterTransaction(BigDecimal totalAmount) {
    super(TransactionType.STORE, totalAmount);
  }

  // No userId for over-the-counter transactions
  @Override
  public Integer getUserId() {
    return null;
  }
}
