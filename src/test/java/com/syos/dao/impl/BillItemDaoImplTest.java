package com.syos.dao.impl;

import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.BillItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillItemDaoImplTest {

  private BillItemDaoImpl billItemDao;
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

    billItemDao = new BillItemDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the mocked static for DatabaseConnection
    mockedDatabaseConnection.close();
  }

  @Test
  public void testSaveBillItem_Success() throws Exception {
    // Arrange
    BillItem billItem = new BillItem(1001, 5, BigDecimal.valueOf(50.00));
    billItem.setBillId(1);
    billItem.setTotalPrice(BigDecimal.valueOf(250.00));

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    // Act
    billItemDao.saveBillItem(billItem);

    // Assert
    verify(mockPreparedStatement, times(1)).setInt(1, billItem.getBillId());
    verify(mockPreparedStatement, times(1)).setInt(2, billItem.getItemId());
    verify(mockPreparedStatement, times(1)).setInt(3, billItem.getQuantity());
    verify(mockPreparedStatement, times(1)).setBigDecimal(4, billItem.getItemPrice());
    verify(mockPreparedStatement, times(1)).setBigDecimal(5, billItem.getTotalPrice());
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testSaveBillItem_Failure_SQLException() throws Exception {
    // Arrange
    BillItem billItem = new BillItem(1001, 5, BigDecimal.valueOf(50.00));
    billItem.setBillId(1);
    billItem.setTotalPrice(BigDecimal.valueOf(250.00));

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeUpdate()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      billItemDao.saveBillItem(billItem);
    });

    assertTrue(thrown.getMessage().contains("Error saving BillItem"));
    verify(mockPreparedStatement, times(1)).executeUpdate();
  }

  @Test
  public void testGetBillItemsByBillId_Success() throws Exception {
    // Arrange
    int billId = 1;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Set up the mock ResultSet to return multiple BillItems
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Simulate 2 rows
    when(mockResultSet.getInt("bill_item_id")).thenReturn(1).thenReturn(2);
    when(mockResultSet.getInt("bill_id")).thenReturn(billId);
    when(mockResultSet.getInt("item_id")).thenReturn(1001).thenReturn(1002);
    when(mockResultSet.getInt("quantity")).thenReturn(5).thenReturn(3);
    when(mockResultSet.getBigDecimal("item_price")).thenReturn(BigDecimal.valueOf(50.00)).thenReturn(BigDecimal.valueOf(30.00));
    when(mockResultSet.getBigDecimal("total_price")).thenReturn(BigDecimal.valueOf(250.00)).thenReturn(BigDecimal.valueOf(90.00));

    // Act
    List<BillItem> billItems = billItemDao.getBillItemsByBillId(billId);

    // Assert
    assertNotNull(billItems);
    assertEquals(2, billItems.size());

    BillItem firstItem = billItems.get(0);
    assertEquals(1001, firstItem.getItemId());
    assertEquals(5, firstItem.getQuantity());
    assertEquals(BigDecimal.valueOf(50.00), firstItem.getItemPrice());
    assertEquals(BigDecimal.valueOf(250.00), firstItem.getTotalPrice());

    BillItem secondItem = billItems.get(1);
    assertEquals(1002, secondItem.getItemId());
    assertEquals(3, secondItem.getQuantity());
    assertEquals(BigDecimal.valueOf(30.00), secondItem.getItemPrice());
    assertEquals(BigDecimal.valueOf(90.00), secondItem.getTotalPrice());

    // Verify interactions
    verify(mockPreparedStatement, times(1)).setInt(1, billId);
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(3)).next(); // Called 3 times: twice for records, once for false
  }

  @Test
  public void testGetBillItemsByBillId_Failure_SQLException() throws Exception {
    // Arrange
    int billId = 1;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      billItemDao.getBillItemsByBillId(billId);
    });

    assertTrue(thrown.getMessage().contains("Error retrieving BillItems for Bill ID: " + billId));
  }
}
