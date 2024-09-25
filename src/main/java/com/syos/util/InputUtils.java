package com.syos.util;

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
}
