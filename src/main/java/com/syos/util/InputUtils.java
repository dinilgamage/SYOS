package com.syos.util;

import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

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

  // Method to validate and get a positive BigDecimal from the user
  public static BigDecimal getValidatedPositiveBigDecimal(Scanner scanner, String promptMessage) {
    BigDecimal value = BigDecimal.ZERO;
    boolean validInput = false;

    while (!validInput) {
      try {
        System.out.print(promptMessage);
        value = scanner.nextBigDecimal();

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
          System.out.println("Please enter a positive value.");
        } else {
          validInput = true;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid numeric value.");
        scanner.next(); // Clear the invalid input from the scanner
      }
    }

    return value;
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

  // New method to validate discount based on the discount type (percentage/fixed)
  public static boolean validateDiscount(String strategyType, BigDecimal discountValue, Inventory inventoryItem) {
    strategyType = strategyType.toLowerCase(); // Normalize strategy type

    // Ensure discount value is positive
    if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
      System.out.println("Discount value must be greater than 0.");
      return false;
    }

    if ("fixed".equals(strategyType)) {
      // For fixed, the discount cannot exceed the item price
      if (discountValue.compareTo(inventoryItem.getPrice()) > 0) {
        System.out.println("Discount cannot exceed the item's price for a fixed discount.");
        return false;
      }
    } else if ("percentage".equals(strategyType)) {
      // For percentage, the discount must not exceed 100
      if (discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
        System.out.println("Percentage discount cannot exceed 100%.");
        return false;
      }
    } else {
      System.out.println("Invalid discount strategy. Please enter either 'percentage' or 'fixed'.");
      return false;
    }

    return true; // Return true if all validations pass
  }

  // New utility method to validate user input against multiple valid options
  public static String getValidatedStringOption(Scanner scanner, String promptMessage, String... validOptions) {
    Set<String> validOptionSet = new HashSet<>();
    for (String option : validOptions) {
      validOptionSet.add(option.toLowerCase());  // Normalize input
    }

    String input = "";
    boolean validInput = false;

    while (!validInput) {
      System.out.print(promptMessage);
      input = scanner.next().toLowerCase(); // Normalize input for case-insensitive comparison

      if (validOptionSet.contains(input)) {
        validInput = true; // If input is valid, break the loop
      } else {
        System.out.println("Invalid input. Please enter one of the following: " + String.join(", ", validOptions));
      }
    }

    return input;
  }
}
