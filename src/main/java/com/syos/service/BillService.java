package com.syos.service;

import com.syos.builder.BillBuilder;
import com.syos.dao.BillDao;
import com.syos.dao.BillItemDao;
import com.syos.dao.InventoryDao;
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
  public Bill buildBill(List<BillItem> items, String transactionType, Integer userId) {
    return buildBill(items, transactionType, null, userId);
  }

  // Overloaded method to handle in-store transactions
  public Bill buildBill(List<BillItem> items, String transactionType, BigDecimal cashTendered) {
    return buildBill(items, transactionType, cashTendered, null);  // No userId for in-store transactions
  }

  // Core method that handles both online and in-store transactions
  public Bill buildBill(List<BillItem> items, String transactionType, BigDecimal cashTendered, Integer userId) {
    BillBuilder billBuilder = new BillBuilder();

    // Create a new transaction (pass userId for online, null for in-store)
    Transaction transaction = transactionService.createTransaction(transactionType, calculateTotal(items), userId);

    // Initialize the BillBuilder
    billBuilder.setTransactionId(transaction.getTransactionId());

    // Add items to the bill, and apply any discount strategy if it exists
    for (BillItem item : items) {
      // Retrieve the inventory item to get discount details
      Inventory inventory = inventoryDao.getItemByCode(item.getItemCode());

      System.out.println(inventory.getItemId());

      // Ensure that the itemId is set in the BillItem before saving
      item.setItemId(inventory.getItemId()); // Set the itemId after retrieving the inventory

      // Use the factory to get the appropriate discount strategy
      DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(inventory);

      // Apply the discount to the total price of the item
      BigDecimal discountedPrice = discountStrategy.applyDiscount(item.getItemPrice());
      item.setItemPrice(discountedPrice);

        // Add the item to the bill
      billBuilder.addItem(item);

      // Update the inventory stock after the purchase
      inventoryService.updateInventoryStock(item.getItemCode(), item.getQuantity(), transactionType);
    }

    // For in-store (over-the-counter) transactions, handle cash tendered and change amount
    if ("over-the-counter".equals(transactionType) && cashTendered != null) {
      BigDecimal totalAmount = calculateTotal(items);
      BigDecimal changeAmount = cashTendered.subtract(totalAmount);

      // Set cashTendered and changeAmount for in-store transactions
      billBuilder.setCashTendered(cashTendered).setChangeAmount(changeAmount);
    }

    // Finalize the bill
    Bill bill = billBuilder.build();

    // Persist the bill and bill items
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

  // Helper method to calculate total bill amount
  private BigDecimal calculateTotal(List<BillItem> items) {
    return items.stream()
      .map(BillItem::getItemPrice)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
