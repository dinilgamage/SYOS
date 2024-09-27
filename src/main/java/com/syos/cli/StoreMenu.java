package com.syos.cli;

import com.syos.processor.BillingProcessor;
import com.syos.processor.ReportProcessor;
import com.syos.processor.DiscountProcessor;
import com.syos.processor.ShelfRestockProcessor;
import com.syos.util.InputUtils;

import java.util.Scanner;

public class StoreMenu {

  private final BillingProcessor billProcessor;
  private final ShelfRestockProcessor restockProcessor;
  private final ReportProcessor reportProcessor;
  private final DiscountProcessor discountProcessor;

  public StoreMenu(BillingProcessor billProcessor, ShelfRestockProcessor restockProcessor,
    ReportProcessor reportProcessor, DiscountProcessor discountProcessor) {
    this.billProcessor = billProcessor;
    this.restockProcessor = restockProcessor;
    this.reportProcessor = reportProcessor;
    this.discountProcessor = discountProcessor;
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

      choice = InputUtils.getValidatedPositiveInt(scanner, "Please choose an option: ");

      switch (choice) {
        case 1:
          // Over the counter bills do not have a user so user id is set to null
          billProcessor.processBilling(scanner, "over-the-counter", null);  // Delegating to BillProcessor,
          break;
        case 2:
          restockProcessor.restockShelf(scanner);  // Delegating to RestockProcessor
          break;
        case 3:
          reportProcessor.generateReports(scanner);  // Delegating to ReportProcessor
          break;
        case 4:
          discountProcessor.addDiscounts(scanner);  // Delegating to DiscountProcessor
          break;
        case 5:
          System.out.println("Exiting store menu...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 5);
  }
}
