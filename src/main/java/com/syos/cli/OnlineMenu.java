package com.syos.cli;

import com.syos.facade.StoreFacade;
import com.syos.model.BillItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OnlineMenu {

  private final StoreFacade storeFacade;

  public OnlineMenu(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void displayOnlineMenu() {
    Scanner scanner = new Scanner(System.in);
    int choice;

//    do {
//      System.out.println("=== Online Store ===");
//      System.out.println("[1] Register");
//      System.out.println("[2] Login");
//      System.out.println("[3] Exit");
//
//      choice = scanner.nextInt();
//
//      switch (choice) {
//        case 1:
//          registerUser(scanner);
//          break;
//        case 2:
//          loginUser(scanner);
//          break;
//        case 3:
//          System.out.println("Returning to Main Menu...");
//          break;
//        default:
//          System.out.println("Invalid option. Please try again.");
//      }
//    } while (choice != 3);
  }

//  private void registerUser(Scanner scanner) {
//    System.out.println("=== Register ===");
//    System.out.print("Enter Name: ");
//    String name = scanner.next();
//    System.out.print("Enter Email: ");
//    String email = scanner.next();
//    System.out.print("Enter Password: ");
//    String password = scanner.next();
//
//    // Call StoreFacade to register user
//    storeFacade.registerUser(name, email, password);
//    System.out.println("Registration Successful!");
//  }

//  private void loginUser(Scanner scanner) {
//    System.out.println("=== Login ===");
//    System.out.print("Enter Email: ");
//    String email = scanner.next();
//    System.out.print("Enter Password: ");
//    String password = scanner.next();
//
//    // Validate login through facade
//    boolean isAuthenticated = storeFacade.loginUser(email, password);
//    if (isAuthenticated) {
//      System.out.println("Login Successful!");
//      userMenu(scanner, email);  // Pass user email for purchase
//    } else {
//      System.out.println("Invalid credentials. Try again.");
//    }
//  }
//
//  private void userMenu(Scanner scanner, String email) {
//    int choice;
//    do {
//      System.out.println("=== User Menu ===");
//      System.out.println("[1] Make Purchase");
//      System.out.println("[2] Logout");
//
//      choice = scanner.nextInt();
//
//      switch (choice) {
//        case 1:
//          makePurchase(scanner, email);
//          break;
//        case 2:
//          System.out.println("Logging out...");
//          break;
//        default:
//          System.out.println("Invalid option. Please try again.");
//      }
//    } while (choice != 2);
//  }
//
//  private void makePurchase(Scanner scanner, String email) {
//    List<BillItem> billItems = new ArrayList<>();
//    System.out.println("=== Purchase ===");
//    String continuePurchase;
//
//    do {
//      System.out.print("Enter Item Code: ");
//      String itemCode = scanner.next();
//      System.out.print("Enter Quantity: ");
//      int quantity = scanner.nextInt();
//
//      // Add items to bill (you will need to validate and retrieve item details)
//      // Assume for now you have BillItemBuilder to construct the items
//      billItems.add(new BillItem(itemCode, quantity));
//
//      System.out.print("Continue purchase? (yes/no): ");
//      continuePurchase = scanner.next();
//    } while ("yes".equalsIgnoreCase(continuePurchase));
//
//    // Process purchase via StoreFacade
//    storeFacade.generateBill(billItems, "online", null, storeFacade.getUserId(email));
//    System.out.println("Purchase complete!");
//  }
}
