package com.syos.model;

import java.math.BigDecimal;

public class OnlineTransaction extends Transaction {
  private int userId;

  public OnlineTransaction(BigDecimal totalAmount, int userId) {
    super("online", totalAmount);
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

