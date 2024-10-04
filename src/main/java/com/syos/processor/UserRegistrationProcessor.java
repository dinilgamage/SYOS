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

    System.out.print("Enter Name: ");
    String name = scanner.next();

    String email = InputUtils.getValidatedEmail(scanner, "Enter Email: ");
    String password = InputUtils.getValidatedPassword(scanner, "Enter Password: ");

    try {
      storeFacade.registerUser(name, email, password);
      System.out.println("Registration Successful!");
    } catch (UserAlreadyExistsException e) {
      System.out.println("Registration failed: " + e.getMessage());
    }
  }
}
