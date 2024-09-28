package com.syos.model;

import com.syos.enums.TransactionType;

import java.math.BigDecimal;

public class OnlineTransaction extends Transaction {
  private int userId;

  public OnlineTransaction(BigDecimal totalAmount, int userId) {
    super(TransactionType.ONLINE, totalAmount);
    this.userId = userId;
  }

  @Override
  public Integer getUserId() {
    return this.userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}

