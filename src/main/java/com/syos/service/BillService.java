package com.syos.service;

import com.syos.builder.BillBuilder;
import com.syos.dao.BillDao;
import com.syos.dao.BillItemDao;
import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.factory.DiscountStrategyFactory;
import com.syos.model.Bill;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.model.Transaction;
import com.syos.service.TransactionService;
import com.syos.service.InventoryService;
import com.syos.strategy.DiscountStrategy;
import com.syos.strategy.NoDiscountStrategy;

import java.math.BigDecimal;
import java.util.List;

public class BillService {

  private BillDao billDao;
  private BillItemDao billItemDao;
  private TransactionService transactionService;
  private InventoryService inventoryService;  // To get item information including discounts
  private final InventoryDao inventoryDao;

  public BillService(BillDao billDao, BillItemDao billItemDao, TransactionService transactionService,
    InventoryService inventoryService, InventoryDao inventoryDao) {
    this.billDao = billDao;
    this.billItemDao = billItemDao;
    this.transactionService = transactionService;
    this.inventoryService = inventoryService;  // Inject InventoryService
    this.inventoryDao = inventoryDao;
  }

  // For online transactions (with userId), without cashTendered or changeAmount
  public Bill buildBill(List<BillItem> items, TransactionType transactionType, Integer userId) {
    return buildBill(items, transactionType, null, userId);
  }

  // Overloaded method to handle in-store transactions
  public Bill buildBill(List<BillItem> items, TransactionType transactionType, BigDecimal cashTendered) {
    return buildBill(items, transactionType, cashTendered, null);  // No userId for in-store transactions
  }

  // Core method that handles both online and in-store transactions
  public Bill buildBill(List<BillItem> items, TransactionType transactionType, BigDecimal cashTendered, Integer userId) {
    if (transactionType == null) {
      throw new InvalidTransactionTypeException("Transaction type cannot be null.");
    }

    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Bill must contain at least one item");
    }

    // Calculate the total transaction amount
    BigDecimal totalAmount = calculateTransactionTotal(items);

    // For in-store transactions, check if cash tendered is less than the total
    if (TransactionType.STORE.equals(transactionType) && cashTendered != null) {
      if (cashTendered.compareTo(totalAmount) < 0) {
        throw new IllegalArgumentException("Cash tendered cannot be less than the total amount.");
      }
    }

    BillBuilder billBuilder = new BillBuilder();

    // Create a new transaction (pass userId for online, null for in-store)
    Transaction transaction = transactionService.createTransaction(transactionType, totalAmount, userId);
    billBuilder.setTransactionId(transaction.getTransactionId());

    // Add items to the bill
    for (BillItem item : items) {
      Inventory inventory = inventoryDao.getItemByCode(item.getItemCode());
      item.setItemId(inventory.getItemId());
      billBuilder.addItem(item);
      inventoryService.updateInventoryStock(item.getItemCode(), item.getQuantity(), transactionType);
    }

    // Handle cash tendered and change for in-store transactions
    if (TransactionType.STORE.equals(transactionType) && cashTendered != null) {
      BigDecimal changeAmount = cashTendered.subtract(totalAmount);
      billBuilder.setCashTendered(cashTendered).setChangeAmount(changeAmount);
    }

    // Finalize the bill
    Bill bill = billBuilder.build();
    saveBill(bill);

    return bill;
  }


  // Method to save the bill
  public void saveBill(Bill bill) {
    // Save the Bill
    billDao.saveBill(bill);

    // Save each BillItem associated with the bill
    for (BillItem item : bill.getBillItems()) {
      item.setBillId(bill.getBillId());
      billItemDao.saveBillItem(item);
    }
  }

  // Helper method to calculate total transaction amount, including quantity
  private BigDecimal calculateTransactionTotal(List<BillItem> items) {
    return items.stream()
      .map(item -> item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity()))) // Price * Quantity
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
