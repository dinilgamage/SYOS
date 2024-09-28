package com.syos.exception;

public class DaoException extends RuntimeException {
  // Constructor with both message and cause
  public DaoException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructor with only message (cause is optional)
  public DaoException(String message) {
    super(message);
  }
}
