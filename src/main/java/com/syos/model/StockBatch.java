package com.syos.model;

import java.time.LocalDate;

public class StockBatch {
  private int batchId;
  private int itemId;
  private int quantity;
  private LocalDate dateReceived;
  private LocalDate expiryDate;

  public StockBatch(int batchId, int itemId, int quantity, LocalDate dateReceived, LocalDate expiryDate) {
    this.itemId = itemId;
    this.quantity = quantity;
    this.dateReceived = dateReceived;
    this.expiryDate = expiryDate;
    this.batchId = batchId;
  }

  // Getters and Setters
  public int getBatchId() {
    return batchId;
  }

  public void setBatchId(int batchId) {
    this.batchId = batchId;
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public LocalDate getDateReceived() {
    return dateReceived;
  }

  public void setDateReceived(LocalDate dateReceived) {
    this.dateReceived = dateReceived;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }
}

