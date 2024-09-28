package com.syos.dao.impl;

import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.enums.TransactionType;
import com.syos.model.OnlineTransaction;
import com.syos.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionDaoImplTest {

  private TransactionDaoImpl transactionDao;
  private Connection mockConnection;
  private PreparedStatement mockPreparedStatement;
  private ResultSet mockResultSet;
  private MockedStatic<DatabaseConnection> mockedDatabaseConnection;

  @BeforeEach
  public void setUp() throws Exception {
    mockConnection = mock(Connection.class);
    mockPreparedStatement = mock(PreparedStatement.class);
    mockResultSet = mock(ResultSet.class);

    // Mock the DatabaseConnection to return the mocked connection
    mockedDatabaseConnection = mockStatic(DatabaseConnection.class);
    mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

    transactionDao = new TransactionDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the mocked static after each test to avoid leakage
    mockedDatabaseConnection.close();
  }

  @Test
  public void testSaveTransaction_Success() throws Exception {
    // Arrange
    Transaction transaction = new OnlineTransaction(BigDecimal.valueOf(100.00), 1);
    transaction.setCreatedAt(LocalDateTime.now());

    when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
      .thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt(1)).thenReturn(10);

    // Act
    transactionDao.saveTransaction(transaction);

    // Assert
    assertEquals(10, transaction.getTransactionId());
    verify(mockPreparedStatement, times(1)).setString(1, "ONLINE");
    verify(mockPreparedStatement, times(1)).setInt(2, 1);
    verify(mockPreparedStatement, times(1)).setBigDecimal(3, BigDecimal.valueOf(100.00));
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testSaveTransaction_NoRowsAffected() throws Exception {
    // Arrange
    Transaction transaction = new OnlineTransaction(BigDecimal.valueOf(100.00), 1);
    transaction.setCreatedAt(LocalDateTime.now());

    when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
      .thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      transactionDao.saveTransaction(transaction);
    });

    assertEquals("Error saving transaction for type: ONLINE", thrown.getMessage());
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testGetTransactionsByDate_Success() throws Exception {
    // Arrange
    LocalDate date = LocalDate.now();
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1).thenReturn(2);
    when(mockResultSet.getString("transaction_type")).thenReturn("ONLINE").thenReturn("STORE");
    when(mockResultSet.getInt("user_id")).thenReturn(1).thenReturn(0);
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(100.00));
    when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // Act
    List<Transaction> transactions = transactionDao.getTransactionsByDate(date);

    // Assert
    assertNotNull(transactions);
    assertEquals(2, transactions.size());
    verify(mockPreparedStatement, times(1)).setDate(1, Date.valueOf(date));
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testGetTransactionsByDate_NoResults() throws Exception {
    // Arrange
    LocalDate date = LocalDate.now();
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(false);

    // Act
    List<Transaction> transactions = transactionDao.getTransactionsByDate(date);

    // Assert
    assertNotNull(transactions);
    assertTrue(transactions.isEmpty());
    verify(mockPreparedStatement, times(1)).setDate(1, Date.valueOf(date));
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testGetTransactionsByDateAndType_Success() throws Exception {
    // Arrange
    LocalDate date = LocalDate.now();
    TransactionType type = TransactionType.ONLINE;

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1);
    when(mockResultSet.getString("transaction_type")).thenReturn("ONLINE");
    when(mockResultSet.getInt("user_id")).thenReturn(1);
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(100.00));
    when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // Act
    List<Transaction> transactions = transactionDao.getTransactionsByDateAndType(date, type);

    // Assert
    assertNotNull(transactions);
    assertEquals(1, transactions.size());
    verify(mockPreparedStatement, times(1)).setDate(1, Date.valueOf(date));
    verify(mockPreparedStatement, times(1)).setString(2, "ONLINE");
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testGetTransactionsByDateAndType_Failure() throws Exception {
    // Arrange
    LocalDate date = LocalDate.now();
    TransactionType type = TransactionType.ONLINE;

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      transactionDao.getTransactionsByDateAndType(date, type);
    });

    assertEquals("Error retrieving transactions for date: " + date + " and type: " + type, thrown.getMessage());
  }

  @Test
  public void testGetAllTransactions_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1);
    when(mockResultSet.getString("transaction_type")).thenReturn("ONLINE");
    when(mockResultSet.getInt("user_id")).thenReturn(1);
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(100.00));
    when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // Act
    List<Transaction> transactions = transactionDao.getAllTransactions();

    // Assert
    assertNotNull(transactions);
    assertEquals(1, transactions.size());
  }

  @Test
  public void testGetAllTransactionsByType_Success() throws Exception {
    // Arrange
    TransactionType type = TransactionType.ONLINE;

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1);
    when(mockResultSet.getString("transaction_type")).thenReturn("ONLINE");
    when(mockResultSet.getInt("user_id")).thenReturn(1);
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(100.00));
    when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // Act
    List<Transaction> transactions = transactionDao.getAllTransactionsByType(type);

    // Assert
    assertNotNull(transactions);
    assertEquals(1, transactions.size());
  }
}
