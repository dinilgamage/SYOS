package com.syos.model;

import java.math.BigDecimal;

public class CartItem {
  private int userId;
  private String itemCode;
  private String itemName;
  private int quantity;
  private BigDecimal price;

  public CartItem(int userId, String itemCode, String itemName, int quantity, BigDecimal price) {
    this.userId = userId;
    this.itemCode = itemCode;
    this.itemName = itemName;
    this.quantity = quantity;
    this.price = price;
  }

  // Getters and Setters
  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }
}
