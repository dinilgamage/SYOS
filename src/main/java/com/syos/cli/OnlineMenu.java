package com.syos.cli;

import com.syos.processor.UserRegistrationProcessor;
import com.syos.processor.UserLoginProcessor;
import com.syos.processor.UserMenuProcessor;
import com.syos.util.InputUtils;

import java.util.Scanner;

public class OnlineMenu {

  private final UserRegistrationProcessor userRegistrationProcessor;
  private final UserLoginProcessor userLoginProcessor;
  private final UserMenuProcessor userMenuProcessor;

  public OnlineMenu(UserRegistrationProcessor userRegistrationProcessor,
                    UserLoginProcessor userLoginProcessor,
                    UserMenuProcessor userMenuProcessor) {
    this.userRegistrationProcessor = userRegistrationProcessor;
    this.userLoginProcessor = userLoginProcessor;
    this.userMenuProcessor = userMenuProcessor;
  }

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
          userRegistrationProcessor.registerUser(scanner);
          break;
        case 2:
          String email = userLoginProcessor.loginUser(scanner);
          if (email != null) {
            System.out.println("Login Successful!");
            userMenuProcessor.displayUserMenu(scanner, email);
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
