package com.syos.factory;

import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;

import java.math.BigDecimal;

public class TransactionFactory {

  /**
   * Factory method to create a Transaction based on the type.
   *
   * @param type - The type of transaction (e.g., 'online', 'over-the-counter').
   * @param totalAmount - The total amount for the transaction.
   * @param userId - The ID of the user (only for online transactions, null for over-the-counter).
   * @return - A new Transaction object.
   */
  public static Transaction createTransaction(String type, BigDecimal totalAmount, Integer userId) {
    switch (type.toLowerCase()) {
      case "online":
        if (userId == null) {
          throw new IllegalArgumentException("User ID is required for online transactions");
        }
        return new OnlineTransaction(totalAmount, userId);
      case "over-the-counter":
        return new OverTheCounterTransaction(totalAmount);
      default:
        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }
  }
}
