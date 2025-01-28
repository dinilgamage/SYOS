package com.syos.model;

public class OrderItem {
  private int orderItemId;
  private int orderId;
  private String itemCode;
  private String itemName;
  private double price;
  private int quantity;
  private double subtotal;

  public OrderItem() {
  }

  public OrderItem(int orderId, String itemCode, String itemName, double price, int quantity) {
    this.orderId = orderId;
    this.itemCode = itemCode;
    this.itemName = itemName;
    this.price = price;
    this.quantity = quantity;
    this.subtotal = price * quantity;
  }

  // Getters and Setters
  public int getOrderItemId() {
    return orderItemId;
  }
  public void setOrderItemId(int orderItemId) {
    this.orderItemId = orderItemId;
  }
  public int getOrderId() {
    return orderId;
  }
  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }
  public String getProductId() {
    return itemCode;
  }
  public void setProductId(String itemCode) {
    this.itemCode = itemCode;
  }
  public String getProductName() {
    return itemName;
  }
  public void setProductName(String itemName) {
    this.itemName = itemName;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }
  public int getQuantity() {
    return quantity;
  }
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  public double getSubtotal() {
    return subtotal;
  }
  public void setSubtotal(double subtotal) {
    this.subtotal = subtotal;
  }

}