package com.syos.enums;

public enum TransactionType {
  ONLINE,
  STORE;

  // Convert a String to TransactionType
  public static TransactionType fromString(String type) {
    switch (type.toLowerCase()) {
      case "online":
        return ONLINE;
      case "store":
        return STORE;
      default:
        throw new IllegalArgumentException("Unknown transaction type: " + type);
    }
  }
}
