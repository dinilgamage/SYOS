package com.syos.enums;

public enum ShelfType {
  ONLINE_SHELF,
  STORE_SHELF;

  // Method to convert string to ShelfType enum
  public static ShelfType fromString(String shelfTypeString) {
    switch (shelfTypeString.toLowerCase()) {
      case "store":
        return STORE_SHELF;
      case "online":
        return ONLINE_SHELF;
      default:
        throw new IllegalArgumentException("Invalid shelf type: " + shelfTypeString);
    }
  }
}
