package com.syos.enums;

public enum DiscountType {
  FIXED,
  PERCENTAGE,
  NONE;

  // Convert a String to DiscountType
  public static DiscountType fromString(String type) {
    if (type == null) {
      return NONE;
    }
    switch (type.toLowerCase()) {
      case "fixed":
        return FIXED;
      case "percentage":
        return PERCENTAGE;
      case "none":
        return NONE;
      default:
        throw new IllegalArgumentException("Unknown discount type: " + type);
    }
  }
}
