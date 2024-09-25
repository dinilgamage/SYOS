package com.syos.cli;

import com.syos.enums.ReportType;
import com.syos.facade.StoreFacade;
import com.syos.model.BillItem;
import com.syos.enums.TransactionType;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StoreMenu {

  private final StoreFacade storeFacade;

  public StoreMenu(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void displayStoreMenu() {
    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
      System.out.println("=== Store Menu ===");
      System.out.println("[1] Billing");
      System.out.println("[2] Restock Shelf");
      System.out.println("[3] Generate Reports");
      System.out.println("[4] Add Discounts");
      System.out.println("[5] Exit");

      choice = scanner.nextInt();

      switch (choice) {
        case 1:
          processBilling(scanner);
          break;
        case 2:
          restockShelf(scanner);
          break;
        case 3:
          generateReports(scanner);
          break;
        case 4:
          addDiscounts(scanner);
          break;
        case 5:
          System.out.println("Returning to Main Menu...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 5);
  }

  private void processBilling(Scanner scanner) {
    List<BillItem> billItems = new ArrayList<>();
    System.out.println("=== Billing ===");
    boolean billingFailed = false; // Flag to track if any issue occurs
    BigDecimal totalAmount = BigDecimal.ZERO; // Store the total amount of the bill after discounts

    String continueBilling;
    do {
      // Use the utility method to validate and get the item
      Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

      // Use the utility method to validate and get the quantity
      int quantity = InputUtils.getValidatedPositiveInt(scanner, "Enter Quantity: ");

      // Check if the stock is available
      boolean isStockAvailable = storeFacade.checkAvailableStock(inventoryItem, quantity, "over-the-counter");
      if (!isStockAvailable) {
        System.out.println("Insufficient stock for item: " + inventoryItem.getItemCode());
        billingFailed = true; // Mark billing as failed if stock is insufficient
        break; // Exit the loop as stock is insufficient
      }

      // Fetch item price and add to the bill if item exists and stock is sufficient
      BigDecimal itemPrice = inventoryItem.getPrice();
      BillItem billItem = new BillItem(inventoryItem.getItemCode(), quantity, itemPrice);

      // Apply discount strategy to the item before calculating the total
      BigDecimal discountedPrice = storeFacade.applyDiscount(inventoryItem, billItem);
      billItem.setItemPrice(discountedPrice);  // Set the discounted price

      billItems.add(billItem);

      // Update the total amount based on the discounted price and quantity
      totalAmount = totalAmount.add(discountedPrice.multiply(BigDecimal.valueOf(quantity)));

      System.out.println("Added BillItem: Code = " + billItem.getItemCode() +
        ", Quantity = " + billItem.getQuantity());

      System.out.print("Continue billing? (yes/no): ");
      continueBilling = scanner.next();
    } while ("yes".equalsIgnoreCase(continueBilling));

    // Only proceed to enter cash tendered if all items have sufficient stock and exist in inventory
    if (!billingFailed) {

      System.out.println("Total amount to be paid (including discounts): " + totalAmount);

      // Use the utility method to validate the cash tendered
      BigDecimal cashTendered = InputUtils.getValidatedCashTendered(scanner, "Enter Cash Tendered: ", totalAmount);

      // Calculate and display change amount
      BigDecimal changeAmount = cashTendered.subtract(totalAmount);
      System.out.println("Change to be returned: " + changeAmount);

      // Process the bill via StoreFacade
      storeFacade.generateBill(billItems, "over-the-counter", cashTendered, null);
      System.out.println("Billing complete! Thanks for shopping with SYOS");
    } else {
      System.out.println("Billing process stopped due to issues with item availability or stock.");
    }
  }

  private void restockShelf(Scanner scanner) {
    System.out.println("=== Restock Shelf ===");

    // Use the utility method to validate and get the item
    Inventory inventoryItem = InputUtils.getValidatedInventoryItem(storeFacade, scanner, "Enter Item Code: ");

    // Use the utility method to validate and get the quantity
    int quantity = InputUtils.getValidatedPositiveInt(scanner, "Enter Quantity: ");

    System.out.print("Enter Shelf Type (store/online): ");
    String shelfType = scanner.next();

    // Restock the item using StoreFacade
    storeFacade.restockItem(inventoryItem.getItemCode(), quantity, shelfType);
    System.out.println("Restocking complete for Item: " + inventoryItem.getItemCode());
  }

  private void generateReports(Scanner scanner) {
    System.out.println("=== Generate Reports ===");
    System.out.println("[1] Total Sales Report");
    System.out.println("[2] Reshelve Report");
    System.out.println("[3] Reorder Report");
    System.out.println("[4] Stock Report");
    System.out.println("[5] Bill Report");

    int reportChoice = scanner.nextInt();
    ReportType reportType;

    switch (reportChoice) {
      case 1:
        reportType = ReportType.TOTAL_SALES;
        break;
      case 2:
        reportType = ReportType.RESHELVE;
        break;
      case 3:
        reportType = ReportType.REORDER;
        break;
      case 4:
        reportType = ReportType.STOCK;
        break;
      case 5:
        reportType = ReportType.BILL;
        break;
      default:
        System.out.println("Invalid report choice.");
        return;
    }

    System.out.print("Enter Date (yyyy-mm-dd): ");
    String dateInput = scanner.next();
    LocalDate date = LocalDate.parse(dateInput);

    System.out.print("Transaction Mode (online/in-store/both): ");
    String transactionMode = scanner.next();

    // Process report via StoreFacade
    storeFacade.generateReport(reportType, date, TransactionType.valueOf(transactionMode.toUpperCase()));
  }

  private void addDiscounts(Scanner scanner) {
    System.out.println("=== Add Discounts ===");
    System.out.print("Enter Item Code: ");
    String itemCode = scanner.next();
    System.out.print("Enter Discount Strategy (percentage/fixed): ");
    String strategyType = scanner.next();
    System.out.print("Enter Discount Value: ");
    BigDecimal discountValue = scanner.nextBigDecimal();

    // Call StoreFacade to apply the discount using the chosen strategy
    storeFacade.addDiscount(itemCode, discountValue, strategyType);
    System.out.println("Discount added!");
  }
}
