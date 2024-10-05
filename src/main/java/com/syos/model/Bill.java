package com.syos.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bill {
  private int billId;
  private int transactionId;
  private BigDecimal totalAmount;
  private BigDecimal cashTendered;
  private BigDecimal changeAmount;
  private List<BillItem> billItems;

  public Bill(int transactionId, BigDecimal totalAmount, BigDecimal cashTendered, BigDecimal changeAmount) {
    this.transactionId = transactionId;
    this.totalAmount = totalAmount;
    this.cashTendered = cashTendered;
    this.changeAmount = changeAmount;
    this.billItems = new ArrayList<>();
  }

  public Bill(int transactionId, BigDecimal totalAmount) {
    this(transactionId, totalAmount, null, null);
  }

  // Getters and Setters
  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public int getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public BigDecimal getCashTendered() {
    return cashTendered;
  }

  public void setCashTendered(BigDecimal cashTendered) {
    this.cashTendered = cashTendered;
  }

  public BigDecimal getChangeAmount() {
    return changeAmount;
  }

  public void setChangeAmount(BigDecimal changeAmount) {
    this.changeAmount = changeAmount;
  }

  // Getter for BillItems
  public List<BillItem> getBillItems() {
    return billItems;
  }

  // Add item to the Bill
  public void addBillItem(BillItem item) {
    this.billItems.add(item);
  }
}
