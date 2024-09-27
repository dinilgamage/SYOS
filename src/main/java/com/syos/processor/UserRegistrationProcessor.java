package com.syos.processor;

import com.syos.exception.UserAlreadyExistsException;
import com.syos.facade.StoreFacade;
import com.syos.util.InputUtils;

import java.util.Scanner;

public class UserRegistrationProcessor {

  private final StoreFacade storeFacade;

  public UserRegistrationProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  public void registerUser(Scanner scanner) {
    System.out.println("=== Register ===");

    // Validate name
    System.out.print("Enter Name: ");
    String name = scanner.next();

    // Validate email and password
    String email = InputUtils.getValidatedEmail(scanner, "Enter Email: ");
    String password = InputUtils.getValidatedPassword(scanner, "Enter Password: ");

    // Call StoreFacade to register user
    try {
      storeFacade.registerUser(name, email, password);
      System.out.println("Registration Successful!");
    } catch (UserAlreadyExistsException e) {
      System.out.println("Registration failed: " + e.getMessage());
    }
  }
}
