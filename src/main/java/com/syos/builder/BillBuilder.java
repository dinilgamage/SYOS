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

  // Constructor to initialize necessary fields
  public BillBuilder() {
    this.billItems = new ArrayList<>();
    this.totalAmount = BigDecimal.ZERO;
  }

  /**
   * Adds an item to the bill.
   *
   * @param item - The BillItem object representing the item being purchased.
   * @return - Returns the BillBuilder instance for method chaining.
   */
  public BillBuilder addItem(BillItem item) {
    this.billItems.add(item);

    // Update the total amount of the bill
    this.totalAmount = this.totalAmount.add(item.getTotalPrice());

    return this;
  }

  /**
   * Sets the transaction ID for the bill.
   *
   * @param transactionId - The transaction ID associated with this bill.
   * @return - Returns the BillBuilder instance for method chaining.
   */
  public BillBuilder setTransactionId(int transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  /**
   * Sets the cash tendered (for in-store payments).
   *
   * @param cashTendered - The amount of cash tendered by the customer.
   * @return - Returns the BillBuilder instance for method chaining.
   */
  public BillBuilder setCashTendered(BigDecimal cashTendered) {
    this.cashTendered = cashTendered;
    return this;
  }

  /**
   * Sets the change amount to be returned to the customer.
   *
   * @param changeAmount - The calculated change amount.
   * @return - Returns the BillBuilder instance for method chaining.
   */
  public BillBuilder setChangeAmount(BigDecimal changeAmount) {
    this.changeAmount = changeAmount;
    return this;
  }

  /**
   * Builds the final Bill object with all the added items and attributes.
   *
   * @return - The constructed Bill object.
   */
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
