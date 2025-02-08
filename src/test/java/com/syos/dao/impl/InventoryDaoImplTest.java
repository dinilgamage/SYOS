package com.syos.dao.impl;

import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryDaoImplTest {

  private InventoryDaoImpl inventoryDao;
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
    when(DatabaseConnection.getConnection()).thenReturn(mockConnection);

    inventoryDao = new InventoryDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the mocked static for DatabaseConnection
    mockedDatabaseConnection.close();
  }

  // Test: getItemByCode() Happy Path
  @Test
  public void testGetItemByCode_Success() throws Exception {
    // Arrange
    String itemCode = "ITEM001";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("item_id")).thenReturn(1);
    when(mockResultSet.getString("item_code")).thenReturn(itemCode);
    when(mockResultSet.getString("name")).thenReturn("Item 1");
    when(mockResultSet.getBigDecimal("price")).thenReturn(BigDecimal.valueOf(100.00));

    // Act
    Inventory item = inventoryDao.getItemByCode(itemCode);

    // Assert
    assertNotNull(item);
    assertEquals(itemCode, item.getItemCode());
  }

  // Test: getItemByCode() No Result
  @Test
  public void testGetItemByCode_NoResult() throws Exception {
    // Arrange
    String itemCode = "ITEM001";
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(false); // No item found

    // Act
    Inventory item = inventoryDao.getItemByCode(itemCode);

    // Assert
    assertNull(item);
  }

  // Test: getItemByCode() Exception Case
  @Test
  public void testGetItemByCode_Exception() throws Exception {
    // Arrange
    String itemCode = "ITEM001";
    when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

    // Act & Assert
    assertThrows(DaoException.class, () -> inventoryDao.getItemByCode(itemCode));
  }

  // Test: getItemById() Happy Path
  @Test
  public void testGetItemById_Success() throws Exception {
    // Arrange
    int itemId = 1;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("item_id")).thenReturn(itemId);

    // Act
    Inventory item = inventoryDao.getItemById(itemId);

    // Assert
    assertNotNull(item);
    assertEquals(itemId, item.getItemId());
  }

  // Test: getAllItems() Happy Path
  @Test
  public void testGetAllItems_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two items
    when(mockResultSet.getString("item_code")).thenReturn("ITEM001").thenReturn("ITEM002");

    // Act
    List<Inventory> items = inventoryDao.getAllItems();

    // Assert
    assertNotNull(items);
    assertEquals(2, items.size());
  }

  // Test: updateInventory() Happy Path
  @Test
  public void testUpdateInventory_Success() throws Exception {
    // Arrange
    Inventory item = new Inventory("ITEM001", "Item 1", "Description", "Category", BigDecimal.valueOf(100.00), 100, 50, 150);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Successful update

    // Act
    inventoryDao.updateInventory(item);

    // Assert
    verify(mockPreparedStatement, times(1)).setInt(1, item.getStoreStock());
    verify(mockPreparedStatement, times(1)).setInt(2, item.getOnlineStock());
    verify(mockPreparedStatement, times(1)).setString(3, item.getDiscountType().toString());
    verify(mockPreparedStatement, times(1)).setBigDecimal(4, item.getDiscountValue());
    verify(mockPreparedStatement, times(1)).setString(5, item.getItemCode());
    verify(mockConnection, times(1)).commit();
  }

  // Test: getLowStockItems() Happy Path
  @Test
  public void testGetLowStockItems_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One low-stock item

    // Act
    List<Inventory> lowStockItems = inventoryDao.getLowStockItems();

    // Assert
    assertNotNull(lowStockItems);
    assertEquals(1, lowStockItems.size());
  }

  // Test: getItemsBelowReorderLevel() Happy Path
  @Test
  public void testGetItemsBelowReorderLevel_Success() throws Exception {
    // Arrange
    int threshold = 100;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One item below reorder level

    // Act
    List<Inventory> items = inventoryDao.getItemsBelowReorderLevel(threshold);

    // Assert
    assertNotNull(items);
    assertEquals(1, items.size());
  }

  // Test: getItemsToReshelveForInStore() Happy Path
  @Test
  public void testGetItemsToReshelveForInStore_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One item to reshelve

    // Act
    List<Inventory> items = inventoryDao.getItemsToReshelveForInStore();

    // Assert
    assertNotNull(items);
    assertEquals(1, items.size());
  }

  // Test: getItemsToReshelveForBoth() Happy Path
  @Test
  public void testGetItemsToReshelveForBoth_Success() throws Exception {
    // Arrange
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One item for both

    // Act
    List<Inventory> items = inventoryDao.getItemsToReshelveForBoth();

    // Assert
    assertNotNull(items);
    assertEquals(1, items.size());
  }
}
