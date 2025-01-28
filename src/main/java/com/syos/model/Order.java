package com.syos.model;

import java.util.Date;

public class Order {
  private int orderId;
  private int customerId;
  private int transactionId;
  private Date orderDate;
  private Date deliveryDate;
  private double totalAmount;
  private String paymentMethod;
  private String orderStatus;
  private String shippingAddress;
  private String billingAddress;

  public Order() {
  }

  public Order(int customerId, int transactionId, Date orderDate, Date deliveryDate, double totalAmount, String paymentMethod, String orderStatus, String shippingAddress, String billingAddress) {
    this.customerId = customerId;
    this.transactionId = transactionId;
    this.orderDate = orderDate;
    this.deliveryDate = deliveryDate;
    this.totalAmount = totalAmount;
    this.paymentMethod = paymentMethod;
    this.orderStatus = orderStatus;
    this.shippingAddress = shippingAddress;
    this.billingAddress = billingAddress;
  }

  // generate all the getters and setters
  public int getOrderId() {
    return orderId;
  }
  public int getCustomerId() {
    return customerId;
  }
  public int getTransactionId() {
    return transactionId;
  }
  public Date getOrderDate() {
    return orderDate;
  }
  public Date getDeliveryDate() {
    return deliveryDate;
  }
  public double getTotalAmount() {
    return totalAmount;
  }
  public String getPaymentMethod() {
    return paymentMethod;
  }
  public String getOrderStatus() {
    return orderStatus;
  }
  public String getShippingAddress() {
    return shippingAddress;
  }
  public String getBillingAddress() {
    return billingAddress;
  }
  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }
  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }
  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }
  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }
  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }
  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }
  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
  public void setShippingAddress(String shippingAddress) {
    this.shippingAddress = shippingAddress;
  }
  public void setBillingAddress(String billingAddress) {
    this.billingAddress = billingAddress;
  }
}