package com.syos.enums;

public enum StockThreshold {
  REORDER_LEVEL(50); // The threshold for reordering stock

  private final int value;

  StockThreshold(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
