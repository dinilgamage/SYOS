package com.syos.builder;

import com.syos.model.Bill;
import com.syos.model.BillItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillBuilder {

  private int transactionId;
  private BigDecimal totalAmount;
  private BigDecimal cashTendered;
  private BigDecimal changeAmount;
  private List<BillItem> billItems;

  public BillBuilder() {
    this.billItems = new ArrayList<>();
    this.totalAmount = BigDecimal.ZERO;
  }

  public BillBuilder addItem(BillItem item) {
    this.billItems.add(item);

    // Update the total amount of the bill
    this.totalAmount = this.totalAmount.add(item.getTotalPrice());

    return this;
  }

  public BillBuilder setTransactionId(int transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  public BillBuilder setCashTendered(BigDecimal cashTendered) {
    this.cashTendered = cashTendered;
    return this;
  }

  public BillBuilder setChangeAmount(BigDecimal changeAmount) {
    this.changeAmount = changeAmount;
    return this;
  }

  public Bill build() {
    // Create the final Bill object
    Bill bill = new Bill(this.transactionId, LocalDate.now(), this.totalAmount, this.cashTendered, this.changeAmount);

    // Add the items to the Bill
    for (BillItem item : this.billItems) {
      bill.addBillItem(item);
    }

    return bill;
  }
}
