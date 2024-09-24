package com.syos.cli;

import java.util.Scanner;

public class MainMenu {

  private final OnlineMenu onlineMenu;
  private final StoreMenu storeMenu;

  public MainMenu(OnlineMenu onlineMenu, StoreMenu storeMenu) {
    this.onlineMenu = onlineMenu;
    this.storeMenu = storeMenu;
  }

  public void displayMenu() {
    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
      System.out.println("=== SYOS Main Menu ===");
      System.out.println("[1] Online Store");
      System.out.println("[2] In-Store Operations");
      System.out.println("[3] Exit");

      choice = scanner.nextInt();

      switch (choice) {
        case 1:
          onlineMenu.displayOnlineMenu();
          break;
        case 2:
          storeMenu.displayStoreMenu();
          break;
        case 3:
          System.out.println("Exiting...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 3);

    scanner.close();
  }
}
