package com.syos.service;

import com.syos.dao.TransactionDao;
import com.syos.enums.TransactionType;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.factory.TransactionFactory;
import com.syos.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionService {

  private TransactionDao transactionDao;

  public TransactionService(TransactionDao transactionDao) {
    this.transactionDao = transactionDao;
  }

  public Transaction createTransaction(TransactionType transactionType, BigDecimal totalAmount, Integer userId) {
    if (transactionType == null) {
      throw new InvalidTransactionTypeException("Transaction type cannot be null");
    }

    Transaction transaction = TransactionFactory.createTransaction(transactionType, totalAmount, userId);

    transactionDao.saveTransaction(transaction);

    return transaction;
  }

  public List<Transaction> getTransactionsByDate(LocalDate date) {
    return transactionDao.getTransactionsByDate(date);
  }
}
