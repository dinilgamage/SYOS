package com.syos.processor;

import com.syos.enums.TransactionType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;
import com.syos.util.InputUtils;

import java.util.List;
import java.util.Scanner;

public class UserMenuProcessor {

  private final StoreFacade storeFacade;
  private final BillingProcessor billingProcessor;

  public UserMenuProcessor(StoreFacade storeFacade, BillingProcessor billingProcessor) {
    this.storeFacade = storeFacade;
    this.billingProcessor = billingProcessor;
  }

  public void displayUserMenu(Scanner scanner, String email) {
    int choice;

    do {
      System.out.println("=== User Menu ===");
      System.out.println("[1] Make Purchase");
      System.out.println("[2] Logout");

      choice = InputUtils.getValidatedPositiveInt(scanner, "Please choose an option: ");

      switch (choice) {
        case 1:
          displayInventoryWithOnlineStock();
          makePurchase(scanner, email);
          break;
        case 2:
          System.out.println("Logging out...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 2);
  }

  private void displayInventoryWithOnlineStock() {
    System.out.println("=== Available Items (Online Stock) ===");
    List<Inventory> items = storeFacade.getAllItems();

    for (Inventory item : items) {
      System.out.println("Item Code: " + item.getItemCode() + ", Name: " + item.getName() + ", Stock: " + item.getOnlineStock());
    }
  }

  private void makePurchase(Scanner scanner, String email) {

    Integer userId = storeFacade.getUserId(email);
    billingProcessor.processBilling(scanner, TransactionType.ONLINE, userId);
  }
}
