package com.syos.dao.impl;

import com.syos.dao.TransactionDao;
import com.syos.database.DatabaseConnection;
import com.syos.enums.TransactionType;
import com.syos.exception.DaoException;
import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {

  private static final String INSERT_TRANSACTION_SQL = "INSERT INTO Transaction (transaction_type, user_id, total_amount, created_at) VALUES (?, ?, ?, ?)";
  private static final String SELECT_TRANSACTIONS_BY_DATE = "SELECT * FROM Transaction WHERE DATE(created_at) = ?";
  private static final String SELECT_TRANSACTIONS_BY_DATE_AND_TYPE = "SELECT * FROM Transaction WHERE DATE(created_at) = ? AND transaction_type = ?";
  private static final String SELECT_ALL_TRANSACTIONS = "SELECT * FROM Transaction";
  private static final String SELECT_TRANSACTIONS_BY_TYPE = "SELECT * FROM Transaction WHERE transaction_type = ?";

  @Override
  public void saveTransaction(Transaction transaction) {
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_SQL, Statement.RETURN_GENERATED_KEYS)) {

      preparedStatement.setString(1, transaction.getTransactionType().toString());
      if (transaction.getUserId() == null || transaction.getUserId() == 0) {
        preparedStatement.setNull(2, Types.INTEGER); // Null if it's an over-the-counter transaction
      } else {
        preparedStatement.setInt(2, transaction.getUserId());
      }
      preparedStatement.setBigDecimal(3, transaction.getTotalAmount());
      preparedStatement.setTimestamp(4, Timestamp.valueOf(transaction.getCreatedAt()));

      int affectedRows = preparedStatement.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            transaction.setTransactionId(generatedKeys.getInt(1));
          }
        }
      } else {
        throw new DaoException("Error saving transaction for type: " + transaction.getTransactionType());
      }
    } catch (SQLException e) {
      throw new DaoException("Error saving transaction for type: " + transaction.getTransactionType(), e);
    }
  }

  @Override
  public List<Transaction> getTransactionsByDate(LocalDate date) {
    List<Transaction> transactions = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSACTIONS_BY_DATE)) {

      preparedStatement.setDate(1, Date.valueOf(date));
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        Transaction transaction = mapRowToTransaction(rs);
        transactions.add(transaction);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving transactions for date: " + date, e);
    }
    return transactions;
  }

  @Override
  public List<Transaction> getTransactionsByDateAndType(LocalDate date, TransactionType type) {
    List<Transaction> transactions = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSACTIONS_BY_DATE_AND_TYPE)) {

      preparedStatement.setDate(1, Date.valueOf(date));
      preparedStatement.setString(2, type.toString());
      ResultSet rs = preparedStatement.executeQuery();

      while (rs.next()) {
        Transaction transaction = mapRowToTransaction(rs);
        transactions.add(transaction);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving transactions for date: " + date + " and type: " + type, e);
    }
    return transactions;
  }

  @Override
  public List<Transaction> getAllTransactions() {
    return getTransactions(SELECT_ALL_TRANSACTIONS, null);
  }

  @Override
  public List<Transaction> getAllTransactionsByType(TransactionType type) {
    return getTransactions(SELECT_TRANSACTIONS_BY_TYPE, type);
  }

  // Helper method to execute the query and map results to Transaction objects
  private List<Transaction> getTransactions(String query, TransactionType type) {
    List<Transaction> transactions = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

      if (type != null) {
        preparedStatement.setString(1, type.toString());
      }

      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        Transaction transaction = mapRowToTransaction(rs);
        transactions.add(transaction);
      }
    } catch (SQLException e) {
      throw new DaoException("Error retrieving transactions", e);
    }
    return transactions;
  }

  // Helper method to map a ResultSet row to a Transaction object
  private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
    int transactionId = rs.getInt("transaction_id");
    String transactionTypeStr = rs.getString("transaction_type");
    int userId = rs.getInt("user_id");
    BigDecimal totalAmount = rs.getBigDecimal("total_amount");
    Timestamp createdAt = rs.getTimestamp("created_at");

    TransactionType transactionType = TransactionType.fromString(transactionTypeStr);  // Convert String to Enum

    Transaction transaction;
    if (TransactionType.ONLINE.equals(transactionType)) {
      // Create an OnlineTransaction
      transaction = new OnlineTransaction(totalAmount, userId);
    } else {
      // Create an OverTheCounterTransaction
      transaction = new OverTheCounterTransaction(totalAmount);
    }

    transaction.setTransactionId(transactionId);
    transaction.setCreatedAt(createdAt.toLocalDateTime());

    return transaction;
  }
}
