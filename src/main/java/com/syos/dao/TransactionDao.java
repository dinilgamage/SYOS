package com.syos.dao;

import com.syos.enums.TransactionType;
import com.syos.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {
  void saveTransaction(Transaction transaction); // Saves a new transaction (online or in-store)
  List<Transaction> getTransactionsByDate(LocalDate date); // Retrieves transactions based on a specific date
  List<Transaction> getTransactionsByDateAndType(LocalDate date, TransactionType type);
  List<Transaction> getAllTransactions(); // A list of all Transaction objects.
  List<Transaction> getAllTransactionsByType(TransactionType type); // A list of Transaction objects filtered by type.

}

