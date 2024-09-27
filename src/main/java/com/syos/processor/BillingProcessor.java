package com.syos.processor;

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

  /**
   * Process billing for both online and in-store transactions.
   *
   * @param scanner        Input scanner for user input.
   * @param transactionType Type of transaction (e.g., "online" or "over-the-counter").
   * @param userId         ID of the user (online customers or in-store as 1 for SYOS Store).
   */
  public void processBilling(Scanner scanner, String transactionType, Integer userId) {
    List<BillItem> billItems = new ArrayList<>();
    System.out.println("=== " + (transactionType.equals("online") ? "Online Billing" : "In-Store Billing") + " ===");
    BigDecimal totalAmount = BigDecimal.ZERO;
    boolean billingFailed = false;

    String continueBilling;
    do {
      BillItem billItem = getValidatedBillItem(scanner, transactionType);  // Step 1: Get item and quantity

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
      processPayment(scanner, billItems, totalAmount, transactionType, userId);  // Step 4: Handle payment and complete billing
    } else {
      System.out.println("Billing process stopped due to issues with item availability or stock.");
    }
  }

  // Adjust this method to check stock based on transaction type (store/online)
  private BillItem getValidatedBillItem(Scanner scanner, String transactionType) {
    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");
    int quantity = InputUtils.getValidatedPositiveInt(scanner, "Enter Quantity: ");

    // Check available stock based on transaction type
    if (!storeFacade.checkAvailableStock(inventoryItem, quantity, transactionType)) {
      System.out.println("Insufficient stock for item: " + inventoryItem.getItemCode());
      return null;
    }

    return processItemForBilling(inventoryItem, quantity);  // Step 2: Process item (apply discounts)
  }

  private BillItem processItemForBilling(Inventory inventoryItem, int quantity) {
    BigDecimal itemPrice = inventoryItem.getPrice();
    BillItem billItem = new BillItem(inventoryItem.getItemCode(), quantity, itemPrice);

    // Apply discount strategy and set the discounted price
    BigDecimal discountedPrice = storeFacade.applyDiscount(inventoryItem, billItem);
    billItem.setItemPrice(discountedPrice);  // Set the discounted price for the item

    System.out.println("Added BillItem: Code = " + billItem.getItemCode() +
      ", Quantity = " + billItem.getQuantity() +
      ", Price = " + discountedPrice);

    return billItem;
  }

  /**
   * Handles the payment process for both online and in-store transactions.
   *
   * @param scanner         Input scanner for user input.
   * @param billItems       List of items in the bill.
   * @param totalAmount     Total amount to be paid after discounts.
   * @param transactionType Type of transaction (e.g., "online" or "over-the-counter").
   * @param userId          ID of the user (online customers or in-store as 1 for SYOS Store).
   */
  private void processPayment(Scanner scanner, List<BillItem> billItems, BigDecimal totalAmount, String transactionType, Integer userId) {
    System.out.println("Total amount to be paid (including discounts): " + totalAmount);

    BigDecimal cashTendered;
    if (transactionType.equals("online")) {
      // For online transactions, there may not be immediate cash handling
      System.out.println("This is an online order. Processing payment...");
      cashTendered = totalAmount;  // Assume full amount for now
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
