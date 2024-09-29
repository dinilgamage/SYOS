package com.syos.exception;

public class InvalidTransactionTypeException extends RuntimeException {
  public InvalidTransactionTypeException(String message) {
    super(message);
  }

  public InvalidTransactionTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
