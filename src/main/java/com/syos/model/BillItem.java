package com.syos.model;

import java.math.BigDecimal;

public class BillItem {
  private int billItemId;
  private int billId;
  private int itemId;
  private String itemCode;
  private int quantity;
  private BigDecimal itemPrice;
  private BigDecimal totalPrice;

  public BillItem(String itemCode, int quantity, BigDecimal itemPrice) {
    this.itemCode = itemCode;
    this.quantity = quantity;
    this.itemPrice = itemPrice;
    this.totalPrice = calculateTotalPrice();
  }

  public BillItem(int itemId, int quantity, BigDecimal itemPrice) {
    this.itemId = itemId;
    this.quantity = quantity;
    this.itemPrice = itemPrice;
    this.totalPrice = calculateTotalPrice();
  }

  // Method to calculate total price without discount logic
  private BigDecimal calculateTotalPrice() {
    return itemPrice.multiply(BigDecimal.valueOf(quantity));
  }

  // Getters and Setters
  public int getBillItemId() {
    return billItemId;
  }

  public void setBillItemId(int billItemId) {
    this.billItemId = billItemId;
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = itemCode;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    this.totalPrice = calculateTotalPrice();
  }

  public BigDecimal getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(BigDecimal itemPrice) {
    this.itemPrice = itemPrice;
    this.totalPrice = calculateTotalPrice();
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }
}
