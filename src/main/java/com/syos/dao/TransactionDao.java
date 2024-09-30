package com.syos.dao;

import com.syos.enums.TransactionType;
import com.syos.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {
  void saveTransaction(Transaction transaction);
  List<Transaction> getTransactionsByDate(LocalDate date);
  List<Transaction> getTransactionsByDateAndType(LocalDate date, TransactionType type);
  List<Transaction> getAllTransactions();
  List<Transaction> getAllTransactionsByType(TransactionType type);

}

