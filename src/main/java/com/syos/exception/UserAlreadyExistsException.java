package com.syos.exception;

// Custom exception for user registration errors
public class UserAlreadyExistsException extends RuntimeException {

  // Constructor that accepts a custom error message
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
