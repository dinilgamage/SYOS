package com.syos.cli;

import com.syos.processor.UserRegistrationProcessor;
import com.syos.processor.UserLoginProcessor;
import com.syos.processor.UserMenuProcessor;
import com.syos.processor.BillingProcessor;
import com.syos.util.InputUtils;

import java.util.Scanner;

public class OnlineMenu {

  private final UserRegistrationProcessor userRegistrationProcessor;
  private final UserLoginProcessor userLoginProcessor;
  private final UserMenuProcessor userMenuProcessor;

  // Constructor that accepts the processors as dependencies
  public OnlineMenu(UserRegistrationProcessor userRegistrationProcessor,
                    UserLoginProcessor userLoginProcessor,
                    UserMenuProcessor userMenuProcessor) {
    this.userRegistrationProcessor = userRegistrationProcessor;
    this.userLoginProcessor = userLoginProcessor;
    this.userMenuProcessor = userMenuProcessor;
  }

  // Orchestrating user interactions
  public void displayOnlineMenu() {
    Scanner scanner = new Scanner(System.in);
    int choice;

    do {
      System.out.println("=== Online Store ===");
      System.out.println("[1] Register");
      System.out.println("[2] Login");
      System.out.println("[3] Exit");

      choice = InputUtils.getValidatedPositiveInt(scanner, "Please choose an option: ");

      switch (choice) {
        case 1:
          userRegistrationProcessor.registerUser(scanner); // Delegate to UserRegistrationProcessor
          break;
        case 2:
          String email = userLoginProcessor.loginUser(scanner);  // Capture email from login
          if (email != null) {  // If login is successful
            System.out.println("Login Successful!");
            userMenuProcessor.displayUserMenu(scanner, email); // Pass the captured email to UserMenuProcessor
          } else {
            System.out.println("Invalid credentials. Try again.");
          }
          break;
        case 3:
          System.out.println("Returning to Main Menu...");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    } while (choice != 3);
  }
}
