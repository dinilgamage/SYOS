package com.syos.service;

import com.syos.dao.TransactionDao;
import com.syos.enums.TransactionType;
import com.syos.exception.InvalidReportTypeException;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.factory.TransactionFactory;
import com.syos.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionService {

  private TransactionDao transactionDao; // For interacting with transaction data

  // Constructor to inject dependencies
  public TransactionService(TransactionDao transactionDao) {
    this.transactionDao = transactionDao;
  }

  /**
   * Creates a new transaction record.
   *
   * @param transactionType - The type of transaction (e.g., 'online', 'over-the-counter').
   * @param totalAmount - The total amount for the transaction.
   * @return - The created Transaction object.
   */
  public Transaction createTransaction(TransactionType transactionType, BigDecimal totalAmount, Integer userId) {
    if (transactionType == null) {
      throw new InvalidTransactionTypeException("Transaction type cannot be null");
    }

    // Use the factory to create the appropriate type of transaction
    Transaction transaction = TransactionFactory.createTransaction(transactionType, totalAmount, userId);

    // Persist the transaction
    transactionDao.saveTransaction(transaction);

    return transaction;
  }

  /**
   * Retrieves transactions for a specific date.
   *
   * @param date - The date for which transactions need to be fetched.
   * @return - A list of Transaction objects for the specified date.
   */
  public List<Transaction> getTransactionsByDate(LocalDate date) {
    return transactionDao.getTransactionsByDate(date);
  }
}
