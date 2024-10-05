package com.syos.factory;

import com.syos.enums.TransactionType;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;

import java.math.BigDecimal;

public class TransactionFactory {

  public static Transaction createTransaction(TransactionType type, BigDecimal totalAmount, Integer userId) {
    if (type == null) {
      throw new InvalidTransactionTypeException("Invalid Transaction Type");
    }
    switch (type) {
      case ONLINE:
        if (userId == null) {
          throw new IllegalArgumentException("User ID is required for online transactions");
        }
        return new OnlineTransaction(totalAmount, userId);
      case STORE:
        return new OverTheCounterTransaction(totalAmount);
      default:
        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }
  }
}
