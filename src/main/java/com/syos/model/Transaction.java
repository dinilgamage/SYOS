package com.syos.model;

import com.syos.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Transaction {
  private int transactionId;
  private TransactionType transactionType;
  private BigDecimal totalAmount;
  private LocalDateTime createdAt;

  // Constructor for all types of transactions
  public Transaction(TransactionType transactionType, BigDecimal totalAmount) {
    this.transactionType = transactionType;
    this.totalAmount = totalAmount;
    this.createdAt = LocalDateTime.now();
  }

  // Abstract method for getting userId, which will be implemented in subclasses
  public abstract Integer getUserId();

  // Getters and Setters
  public int getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
