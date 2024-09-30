package com.syos.processor;

import com.syos.enums.TransactionType;
import com.syos.facade.StoreFacade;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillingProcessor {

  private final StoreFacade storeFacade;

  public BillingProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void processBilling(Scanner scanner, TransactionType transactionType, Integer userId) {
    List<BillItem> billItems = new ArrayList<>();
    System.out.println("=== " + (transactionType.equals(TransactionType.ONLINE) ? "Online Billing" : "In-Store Billing") +
      " ===");
    BigDecimal totalAmount = BigDecimal.ZERO;
    boolean billingFailed = false;

    String continueBilling;
    do {
      BillItem billItem = getValidatedBillItem(scanner, transactionType);

      if (billItem != null) {
        billItems.add(billItem);
        totalAmount = totalAmount.add(billItem.getItemPrice().multiply(BigDecimal.valueOf(billItem.getQuantity())));
      } else {
        billingFailed = true;
        break;
      }

      System.out.print("Continue billing? (yes/no): ");
      continueBilling = scanner.next();
    } while ("yes".equalsIgnoreCase(continueBilling));

    if (!billingFailed) {
      processPayment(scanner, billItems, totalAmount, transactionType, userId);
    } else {
      System.out.println("Billing process stopped due to issues with item availability or stock.");
    }
  }

  private BillItem getValidatedBillItem(Scanner scanner, TransactionType transactionType) {
    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");
    int quantity = InputUtils.getValidatedPositiveInt(scanner, "Enter Quantity: ");

    // Check available stock based on transaction type
    if (!storeFacade.checkAvailableStock(inventoryItem, quantity, transactionType)) {
      System.out.println("Insufficient stock for item: " + inventoryItem.getItemCode());
      return null;
    }

    return processItemForBilling(inventoryItem, quantity);
  }

  private BillItem processItemForBilling(Inventory inventoryItem, int quantity) {
    BigDecimal itemPrice = inventoryItem.getPrice();
    BillItem billItem = new BillItem(inventoryItem.getItemCode(), quantity, itemPrice);

    // Apply discount strategy and set the discounted price
    BigDecimal discountedPrice = storeFacade.applyDiscount(inventoryItem, billItem);
    billItem.setItemPrice(discountedPrice);

    System.out.println("Added BillItem: Code = " + billItem.getItemCode() +
      ", Quantity = " + billItem.getQuantity() +
      ", Price = " + discountedPrice);

    return billItem;
  }

  private void processPayment(Scanner scanner, List<BillItem> billItems, BigDecimal totalAmount, TransactionType transactionType,
    Integer userId) {
    System.out.println("Total amount to be paid (including discounts): " + totalAmount);

    BigDecimal cashTendered;
    if (transactionType.equals(TransactionType.ONLINE)) {
      // For online transactions, there may not be immediate cash handling
      System.out.println("This is an online order. Processing payment...");
      cashTendered = totalAmount;
    } else {
      // For in-store, prompt for cash tendered
      cashTendered = InputUtils.getValidatedCashTendered(scanner, "Enter Cash Tendered: ", totalAmount);

      BigDecimal changeAmount = cashTendered.subtract(totalAmount);
      System.out.println("Change to be returned: " + changeAmount);
    }

    // Process the bill via StoreFacade for both online and in-store transactions
    storeFacade.generateBill(billItems, transactionType, cashTendered, userId);
    System.out.println("Billing complete! Thanks for shopping with SYOS");
  }
}
