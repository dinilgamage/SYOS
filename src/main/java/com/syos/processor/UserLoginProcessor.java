package com.syos.processor;

import com.syos.facade.StoreFacade;

import java.util.Scanner;

public class UserLoginProcessor {

  private final StoreFacade storeFacade;

  public UserLoginProcessor(StoreFacade storeFacade) {
    this.storeFacade = storeFacade;
  }

  // Modified to return the email if login is successful
  public String loginUser(Scanner scanner) {
    System.out.println("=== Login ===");
    System.out.print("Enter Email: ");
    String email = scanner.next();  // Capture the email
    System.out.print("Enter Password: ");
    String password = scanner.next();

    // Validate login through facade
    boolean isAuthenticated = storeFacade.loginUser(email, password);
    if (isAuthenticated) {
      return email;  // Return the email if login is successful
    } else {
      return null;  // Return null if login fails
    }
  }
}
