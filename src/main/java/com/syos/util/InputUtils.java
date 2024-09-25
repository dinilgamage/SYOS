package com.syos.util;

import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtils {

  // Method to validate and get a positive integer from the user
  public static int getValidatedPositiveInt(Scanner scanner, String promptMessage) {
    int number = 0;
    boolean validInput = false;

    while (!validInput) {
      try {
        System.out.print(promptMessage);
        number = scanner.nextInt();

        if (number <= 0) {
          System.out.println("Please enter a positive number.");
        } else {
          validInput = true;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid integer.");
        scanner.next(); // Clear the invalid input
      }
    }

    return number;
  }

  // Method to check if the item exists in the inventory
  public static Inventory getValidatedInventoryItem(StoreFacade storeFacade, Scanner scanner, String promptMessage) {
    Inventory inventoryItem = null;
    while (inventoryItem == null) {
      System.out.print(promptMessage);
      String itemCode = scanner.next();

      // Check if the item exists in the inventory
      inventoryItem = storeFacade.getItemByCode(itemCode);
      if (inventoryItem == null) {
        System.out.println("Item not found: " + itemCode + ". Please try again.");
      }
    }
    return inventoryItem;
  }

  // Method to validate cash tendered, ensuring it's greater than or equal to the total amount
  public static BigDecimal getValidatedCashTendered(Scanner scanner, String promptMessage, BigDecimal totalAmount) {
    BigDecimal cashTendered = BigDecimal.ZERO;
    boolean validInput = false;

    while (!validInput) {
      try {
        System.out.print(promptMessage);
        cashTendered = scanner.nextBigDecimal();

        if (cashTendered.compareTo(totalAmount) < 0) {
          System.out.println("Insufficient cash. Please enter an amount equal to or greater than the total bill.");
        } else {
          validInput = true;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid decimal amount.");
        scanner.next(); // Clear the invalid input
      }
    }

    return cashTendered;
  }
}
