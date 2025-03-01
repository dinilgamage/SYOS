package com.syos.util;

import com.syos.enums.DiscountType;
import com.syos.facade.StoreFacade;
import com.syos.model.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        scanner.next();
      }
    }

    return cashTendered;
  }

  // Method to validate discount based on the discount type (percentage/fixed)
  public static boolean validateDiscount(DiscountType discountType, BigDecimal discountValue, Inventory inventoryItem) {

    if (discountValue.compareTo(BigDecimal.ZERO) <= 0) {
      System.out.println("Discount value must be greater than 0.");
      return false;
    }

    if (DiscountType.FIXED.equals(discountType)) {
      // For fixed, the discount cannot exceed the item price
      if (discountValue.compareTo(inventoryItem.getPrice()) > 0) {
        System.out.println("Discount cannot exceed the item's price for a fixed discount.");
        return false;
      }
    } else if (DiscountType.PERCENTAGE.equals(discountType)) {
      // For percentage, the discount must not exceed 100
      if (discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
        System.out.println("Percentage discount cannot exceed 100%.");
        return false;
      }
    } else {
      System.out.println("Invalid discount strategy. Please enter either 'percentage' or 'fixed'.");
      return false;
    }

    return true;
  }

  // Method to validate user input against multiple valid options
  public static String getValidatedStringOption(Scanner scanner, String promptMessage, String... validOptions) {
    Set<String> validOptionSet = new HashSet<>();
    for (String option : validOptions) {
      validOptionSet.add(option.toLowerCase());  // Normalize input
    }

    String input = "";
    boolean validInput = false;

    while (!validInput) {
      System.out.print(promptMessage);
      input = scanner.next().toLowerCase();

      if (validOptionSet.contains(input)) {
        validInput = true;
      } else {
        System.out.println("Invalid input. Please enter one of the following: " + String.join(", ", validOptions));
      }
    }

    return input;
  }

  // Method to validate and get a LocalDate from the user
  public static LocalDate getValidatedDate(Scanner scanner, String promptMessage) {
    LocalDate date = null;
    boolean validInput = false;

    while (!validInput) {
      try {
        System.out.print(promptMessage);
        String dateInput = scanner.next();
        // Try to parse the date
        date = LocalDate.parse(dateInput);
        validInput = true;
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Please enter the date in the format yyyy-mm-dd.");
      }
    }

    return date;
  }
  // Method to validate email format
  public static String getValidatedEmail(Scanner scanner, String promptMessage) {
    String email = "";
    boolean validInput = false;

    while (!validInput) {
      System.out.print(promptMessage);
      email = scanner.next();

      if (isValidEmail(email)) {
        validInput = true;
      } else {
        System.out.println("Invalid email format. Please enter a valid email.");
      }
    }

    return email;
  }

  // Method to validate password strength (min 8 chars, at least one number and special character)
  public static String getValidatedPassword(Scanner scanner, String promptMessage) {
    String password = "";
    boolean validInput = false;

    while (!validInput) {
      System.out.print(promptMessage);
      password = scanner.next();

      if (isValidPassword(password)) {
        validInput = true;
      } else {
        System.out.println("Password must be at least 8 characters long and include at least one number and one special character.");
      }
    }

    return password;
  }

  // Helper method to check if the email is valid
  private static boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pattern = Pattern.compile(emailRegex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  // Helper method to check if the password meets the required criteria
  private static boolean isValidPassword(String password) {
    String passwordRegex = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
    Pattern pattern = Pattern.compile(passwordRegex);
    Matcher matcher = pattern.matcher(password);
    return matcher.matches();
  }
}
