package com.syos.cli;

import com.syos.exception.UserAlreadyExistsException;
import com.syos.facade.StoreFacade;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.model.User;
import com.syos.processor.BillingProcessor;
import com.syos.util.InputUtils;

import java.util.List;
import java.util.Scanner;

public class OnlineMenu {

  private final StoreFacade storeFacade;
  private final BillingProcessor billingProcessor;  // Reusing BillingProcessor

  public OnlineMenu(StoreFacade storeFacade, BillingProcessor billingProcessor) {
    this.storeFacade = storeFacade;
    this.billingProcessor = billingProcessor; // Initialize BillingProcessor
  }

  public void displayOnlineMenu() {
    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
      System.out.println("=== Online Store ===");
      System.out.println("[1] Register");
      System.out.println("[2] Login");
      System.out.println("[3] Exit");

      choice = scanner.nextInt();

      switch (choice) {
        case 1:
          registerUser(scanner);
          break;
        case 2:
          loginUser(scanner);
          break;
        case 3:
          System.out.println("Returning to Main Menu...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 3);
  }

  private void registerUser(Scanner scanner) {
    System.out.println("=== Register ===");

    // Validate name (similar to other validated inputs)
    System.out.print("Enter Name: ");
    String name = scanner.next();

    // Validate email using InputUtils
    String email = InputUtils.getValidatedEmail(scanner, "Enter Email: ");

    // Validate password using InputUtils
    String password = InputUtils.getValidatedPassword(scanner, "Enter Password: ");

    // Call StoreFacade to register user
    try {
      storeFacade.registerUser(name, email, password);
      System.out.println("Registration Successful!");
    } catch (UserAlreadyExistsException e) {
      // Handle the custom exception and provide meaningful feedback
      System.out.println("Registration failed: " + e.getMessage());
    }
  }


  private void loginUser(Scanner scanner) {
    System.out.println("=== Login ===");
    System.out.print("Enter Email: ");
    String email = scanner.next();
    System.out.print("Enter Password: ");
    String password = scanner.next();

    // Validate login through facade
    boolean isAuthenticated = storeFacade.loginUser(email, password);
    if (isAuthenticated) {
      System.out.println("Login Successful!");
      userMenu(scanner, email);  // Pass user email for purchase
    } else {
      System.out.println("Invalid credentials. Try again.");
    }
  }

  private void userMenu(Scanner scanner, String email) {
    int choice;
    do {
      System.out.println("=== User Menu ===");
      System.out.println("[1] Make Purchase");
      System.out.println("[2] Logout");

      choice = scanner.nextInt();

      switch (choice) {
        case 1:
          displayInventoryWithOnlineStock();  // Display inventory before making a purchase
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
    List<Inventory> items = storeFacade.getAllItems(); // Retrieve all inventory items

    // Display items with their online stock
    for (Inventory item : items) {
      System.out.println("Item Code: " + item.getItemCode() + ", Name: " + item.getName() + ", Stock: " + item.getOnlineStock());
    }
  }

  private void makePurchase(Scanner scanner, String email) {
    // Reusing the existing billingProcessor for online purchases
    Integer userId = storeFacade.getUserId(email);
    billingProcessor.processBilling(scanner, "online", userId);  // Pass 'online' as the transaction type
  }
}
