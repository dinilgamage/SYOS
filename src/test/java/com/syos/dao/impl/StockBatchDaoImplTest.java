package com.syos.dao.impl;

import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.StockBatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockBatchDaoImplTest {

  private StockBatchDaoImpl stockBatchDao;
  private Connection mockConnection;
  private PreparedStatement mockPreparedStatement;
  private ResultSet mockResultSet;
  private MockedStatic<DatabaseConnection> mockedDatabaseConnection;

  @BeforeEach
  public void setUp() throws Exception {
    // Mock the database connection, prepared statement, and result set
    mockConnection = mock(Connection.class);
    mockPreparedStatement = mock(PreparedStatement.class);
    mockResultSet = mock(ResultSet.class);

    // Mock the DatabaseConnection to return the mocked connection
    mockedDatabaseConnection = mockStatic(DatabaseConnection.class);
    mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

    stockBatchDao = new StockBatchDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the mocked static after each test to avoid leakage
    mockedDatabaseConnection.close();
  }

  @Test
  public void testGetBatchesForItem_Success() throws Exception {
    // Arrange
    int itemId = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate two rows in the result set
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("batch_id")).thenReturn(1).thenReturn(2);
    when(mockResultSet.getInt("item_id")).thenReturn(itemId);
    when(mockResultSet.getInt("quantity")).thenReturn(50);
    when(mockResultSet.getDate("date_received")).thenReturn(Date.valueOf(LocalDate.now()));
    when(mockResultSet.getDate("expiry_date")).thenReturn(Date.valueOf(LocalDate.now().plusMonths(1)));

    // Act
    List<StockBatch> batches = stockBatchDao.getBatchesForItem(itemId);

    // Assert
    assertNotNull(batches);
    assertEquals(2, batches.size());

    verify(mockPreparedStatement, times(1)).setInt(1, itemId);
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(3)).next();  // 2 results + 1 false
  }

  @Test
  public void testGetBatchesForItem_NoResults() throws Exception {
    // Arrange
    int itemId = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(false);  // No results

    // Act
    List<StockBatch> batches = stockBatchDao.getBatchesForItem(itemId);

    // Assert
    assertNotNull(batches);
    assertEquals(0, batches.size());  // No batches

    verify(mockPreparedStatement, times(1)).setInt(1, itemId);
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testGetBatchesForItem_Exception() throws Exception {
    // Arrange
    int itemId = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      stockBatchDao.getBatchesForItem(itemId);
    });

    assertEquals("Error retrieving batches for item ID: " + itemId, thrown.getMessage());

    verify(mockPreparedStatement, times(1)).setInt(1, itemId);
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testUpdateBatch_Success() throws Exception {
    // Arrange
    StockBatch batch = new StockBatch(1, 100, 50, LocalDate.now(), LocalDate.now().plusMonths(1));
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    when(mockPreparedStatement.executeUpdate()).thenReturn(1);  // Simulate successful update

    // Act
    stockBatchDao.updateBatch(batch);

    // Assert
    verify(mockPreparedStatement, times(1)).setInt(1, batch.getQuantity());
    verify(mockPreparedStatement, times(1)).setDate(2, Date.valueOf(batch.getExpiryDate()));
    verify(mockPreparedStatement, times(1)).setInt(3, batch.getBatchId());
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testUpdateBatch_NoRowsAffected() throws Exception {
    // Arrange
    StockBatch batch = new StockBatch(1, 100, 50, LocalDate.now(), LocalDate.now().plusMonths(1));
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    when(mockPreparedStatement.executeUpdate()).thenReturn(0);  // Simulate no rows updated

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      stockBatchDao.updateBatch(batch);
    });

    assertEquals("Update failed, no rows affected for batch ID: " + batch.getBatchId(), thrown.getMessage());

    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testUpdateBatch_Exception() throws Exception {
    // Arrange
    StockBatch batch = new StockBatch(1, 100, 50, LocalDate.now(), LocalDate.now().plusMonths(1));
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);  // Simulate an SQL exception

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      stockBatchDao.updateBatch(batch);
    });

    assertEquals("Error updating batch ID: " + batch.getBatchId(), thrown.getMessage());

    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testGetNearestExpiryBatch_Success() throws Exception {
    // Arrange
    int itemId = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate a result in the result set
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("batch_id")).thenReturn(1);
    when(mockResultSet.getInt("item_id")).thenReturn(itemId);
    when(mockResultSet.getInt("quantity")).thenReturn(50);

    // Ensure valid non-null dates are returned
    when(mockResultSet.getDate("date_received")).thenReturn(Date.valueOf(LocalDate.now()));
    when(mockResultSet.getDate("expiry_date")).thenReturn(Date.valueOf(LocalDate.now().plusMonths(1)));

    // Act
    StockBatch batch = stockBatchDao.getNearestExpiryBatch(itemId);

    // Assert
    assertNotNull(batch);
    assertEquals(1, batch.getBatchId());
    assertEquals(itemId, batch.getItemId());
    assertEquals(50, batch.getQuantity());
    assertNotNull(batch.getExpiryDate());  // Ensure the expiry date is not null

    // Verify interactions with mocks
    verify(mockPreparedStatement, times(1)).setInt(1, itemId);
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(1)).next();
  }


  @Test
  public void testGetNearestExpiryBatch_NoResult() throws Exception {
    // Arrange
    int itemId = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(false);  // No result

    // Act
    StockBatch batch = stockBatchDao.getNearestExpiryBatch(itemId);

    // Assert
    assertNull(batch);  // No batch found

    verify(mockPreparedStatement, times(1)).setInt(1, itemId);
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testGetAllStockBatches_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate two rows in the result set
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("batch_id")).thenReturn(1).thenReturn(2);
    when(mockResultSet.getInt("item_id")).thenReturn(100);
    when(mockResultSet.getInt("quantity")).thenReturn(50);
    when(mockResultSet.getDate("date_received")).thenReturn(Date.valueOf(LocalDate.now()));
    when(mockResultSet.getDate("expiry_date")).thenReturn(Date.valueOf(LocalDate.now().plusMonths(1)));

    // Act
    List<StockBatch> stockBatches = stockBatchDao.getAllStockBatches();

    // Assert
    assertNotNull(stockBatches);
    assertEquals(2, stockBatches.size());

    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(3)).next();  // 2 results + 1 false
  }

  @Test
  public void testGetAllStockBatches_Exception() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);  // Simulate SQL exception

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      stockBatchDao.getAllStockBatches();
    });

    assertEquals("Error retrieving all stock batches", thrown.getMessage());

    verify(mockPreparedStatement, times(1)).executeQuery();
  }
}
