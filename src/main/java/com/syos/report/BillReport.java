package com.syos.report;

import java.time.LocalDate;
import java.util.List;

import com.syos.dao.TransactionDao;
import com.syos.enums.ReportFilterType;
import com.syos.enums.TransactionType;
import com.syos.model.Inventory;
import com.syos.model.Transaction;

public class BillReport extends Report {

  private TransactionDao transactionDao;
  private List<Transaction> transactions;

  // Constructor to inject dependencies (TransactionDao)
  public BillReport(TransactionDao transactionDao) {
    this.transactionDao = transactionDao;
  }

  @Override
  protected void prepareHeader() {
    System.out.println("=== Bill Report ===");
  }

  @Override
  protected void collectData(LocalDate date, ReportFilterType type) {
    System.out.println("Collecting all transaction data for " + type);

    // Fetch all transactions based on the type (online, in-store, or both)
    if (type == ReportFilterType.BOTH) {
      transactions = transactionDao.getAllTransactions();
    } else if (type == ReportFilterType.ONLINE) {
      transactions = transactionDao.getAllTransactionsByType(TransactionType.ONLINE);
    } else {
      transactions = transactionDao.getAllTransactionsByType(TransactionType.STORE);
    }
  }

  @Override
  protected void formatReport() {
    System.out.println("Formatting Bill Report...");
  }

  @Override
  protected void displayReport(ReportFilterType reportFilterType) {
    System.out.println("Displaying all transactions:");
    for (Transaction transaction : transactions) {
      System.out.println("Transaction ID: " + transaction.getTransactionId() +
        ", Type: " + transaction.getTransactionType() +
        ", Total Amount: " + transaction.getTotalAmount() +
        ", Date: " + transaction.getCreatedAt());
    }
  }

  // Getter method for getTransactions for testing purposes
  public List<Transaction> getTransactions() {
    return transactions;
  }
}
