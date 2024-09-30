package com.syos.command;

import com.syos.enums.TransactionType;
import com.syos.service.BillService;
import com.syos.model.BillItem;

import java.math.BigDecimal;
import java.util.List;

public class GenerateBillCommand implements Command {

  private BillService billService;
  private List<BillItem> billItems;
  private TransactionType transactionType;
  private BigDecimal cashTendered;  // Optional for in-store transactions
  private Integer userId;           // Optional for online transactions

  // Constructor for online transactions (with userId)
  public GenerateBillCommand(BillService billService, List<BillItem> billItems, TransactionType transactionType, Integer userId) {
    this.billService = billService;
    this.billItems = billItems;
    this.transactionType = transactionType;
    this.userId = userId;
    this.cashTendered = null;  // No cash for online transactions
  }

  // Constructor for in-store transactions (with cashTendered)
  public GenerateBillCommand(BillService billService, List<BillItem> billItems, TransactionType transactionType,
    BigDecimal cashTendered) {
    this.billService = billService;
    this.billItems = billItems;
    this.transactionType = transactionType;
    this.cashTendered = cashTendered;
    this.userId = null;  // No userId for in-store transactions
  }

  @Override
  public void execute() {
    if (TransactionType.STORE.equals(transactionType)) {
      billService.buildBill(billItems, transactionType, cashTendered);
    } else if (TransactionType.ONLINE.equals(transactionType)) {
      if (userId == null) {
        throw new IllegalArgumentException("User ID is required for online transactions.");
      }
      billService.buildBill(billItems, transactionType, userId);
    } else {
      throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
    }
  }
}
