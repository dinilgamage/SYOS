package com.syos.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Order {
  private int orderId;
  private int customerId;
  private int transactionId;

  private String email;
  private String firstName;
  private String lastName;

  private Date orderDate;
  private Date deliveryDate;
  private String orderStatus;
  private String shippingMethod;

  private double totalAmount;
  private String paymentMethod;

  private String address;
  private String apartment;
  private String city;
  private String postalCode;
  private String phone;
  private List<OrderItem> orderItems;

  // Empty constructor
  public Order() {
  }

  // Constructor
  public Order(int orderId,
    int customerId,
    int transactionId,
    Date orderDate,
    Date deliveryDate,
    String email,
    String firstName,
    String lastName,
    double totalAmount,
    String paymentMethod,
    String orderStatus,
    String address,
    String apartment,
    String city,
    String postalCode,
    String phone,
    String shippingMethod) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.transactionId = transactionId;
    this.orderDate = orderDate;
    this.deliveryDate = deliveryDate;
    this.totalAmount = totalAmount;
    this.paymentMethod = paymentMethod;
    this.orderStatus = orderStatus;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.apartment = apartment;
    this.city = city;
    this.postalCode = postalCode;
    this.phone = phone;
    this.shippingMethod = shippingMethod;
  }

  // Getters and Setters
  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void setCustomerId(int customerId) {
    this.customerId = customerId;
  }

  public int getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getApartment() {
    return apartment;
  }

  public void setApartment(String apartment) {
    this.apartment = apartment;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getShippingMethod() {
    return shippingMethod;
  }

  public void setShippingMethod(String shippingMethod) {
    this.shippingMethod = shippingMethod;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount.doubleValue();
  }

  public void setTransactionId(long transactionId) {
    this.transactionId = (int) transactionId;
  }

  public void setOrderDate(Timestamp orderDate) {
    this.orderDate = new Date(orderDate.getTime());
  }

  public void setDeliveryDate(Timestamp deliveryDate) {
    this.deliveryDate = new Date(deliveryDate.getTime());
  }
}