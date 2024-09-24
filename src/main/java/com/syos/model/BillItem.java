package com.syos.model;

import java.math.BigDecimal;

public class BillItem {
  private int billItemId;
  private int billId;
  private int itemId;
  private String itemCode; // Updated from itemId
  private int quantity;
  private BigDecimal itemPrice;
  private BigDecimal totalPrice;
  private BigDecimal discount;

  // Constructors
  public BillItem(String itemCode, int quantity, BigDecimal itemPrice) {
    this.itemCode = itemCode;
    this.quantity = quantity;
    this.itemPrice = itemPrice;
  }

  public BillItem(int billId, int itemId, int quantity, BigDecimal itemPrice, BigDecimal discount) {
    this.billId = billId;
    this.itemId = itemId;
    this.quantity = quantity;
    this.itemPrice = itemPrice;
    this.discount = discount;
    this.totalPrice = calculateTotalPrice();
  }

  // Method to calculate total price after discount
  private BigDecimal calculateTotalPrice() {
    BigDecimal priceWithoutDiscount = itemPrice.multiply(BigDecimal.valueOf(quantity));
    return priceWithoutDiscount.subtract(priceWithoutDiscount.multiply(discount).divide(BigDecimal.valueOf(100)));
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
    this.totalPrice = calculateTotalPrice(); // Recalculate total price
  }

  public BigDecimal getItemPrice() {
    return itemPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(BigDecimal discount) {
    this.discount = discount;
    this.totalPrice = calculateTotalPrice(); // Recalculate total price
  }
}

