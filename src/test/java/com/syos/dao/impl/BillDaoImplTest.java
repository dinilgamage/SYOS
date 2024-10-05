package com.syos.dao.impl;

import com.syos.dao.impl.BillDaoImpl;
import com.syos.database.DatabaseConnection;
import com.syos.exception.DaoException;
import com.syos.model.Bill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillDaoImplTest {

  private BillDaoImpl billDao;
  private Connection mockConnection;
  private PreparedStatement mockPreparedStatement;
  private ResultSet mockResultSet;
  private MockedStatic<DatabaseConnection> mockedDatabaseConnection;

  @BeforeEach
  public void setUp() throws Exception {
    // Mock database connection, prepared statement, and result set
    mockConnection = mock(Connection.class);
    mockPreparedStatement = mock(PreparedStatement.class);
    mockResultSet = mock(ResultSet.class);

    // Mock the static DatabaseConnection to return the mocked connection
    mockedDatabaseConnection = mockStatic(DatabaseConnection.class);
    mockedDatabaseConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);

    billDao = new BillDaoImpl();
  }

  @AfterEach
  public void tearDown() {
    // Close the static mock to avoid interference between tests
    mockedDatabaseConnection.close();
  }

  @Test
  public void testGetBillById_Success() throws Exception {
    int billId = 1;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Setup result set
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt("bill_id")).thenReturn(billId);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1001);
    when(mockResultSet.getDate("bill_date")).thenReturn(Date.valueOf(LocalDate.now()));
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(500.00));
    when(mockResultSet.getBigDecimal("cash_tendered")).thenReturn(BigDecimal.valueOf(600.00));
    when(mockResultSet.getBigDecimal("change_amount")).thenReturn(BigDecimal.valueOf(100.00));

    Bill bill = billDao.getBillById(billId);

    assertNotNull(bill);
    assertEquals(billId, bill.getBillId());
    assertEquals(BigDecimal.valueOf(500.00), bill.getTotalAmount());
    assertEquals(BigDecimal.valueOf(600.00), bill.getCashTendered());
    assertEquals(BigDecimal.valueOf(100.00), bill.getChangeAmount());

    verify(mockPreparedStatement, times(1)).setInt(1, billId);
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(1)).next();
  }

  @Test
  public void testGetBillById_NoResult() throws Exception {
    int billId = 1;
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Simulate no matching result
    when(mockResultSet.next()).thenReturn(false);

    Bill bill = billDao.getBillById(billId);

    assertNull(bill);
    verify(mockPreparedStatement, times(1)).setInt(1, billId);
    verify(mockPreparedStatement, times(1)).executeQuery();
  }

  @Test
  public void testSaveBill_Success() throws Exception {
    Bill bill = new Bill(1001, BigDecimal.valueOf(500.00), BigDecimal.valueOf(600.00), BigDecimal.valueOf(100.00));
    when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);

    // Simulate the successful insertion
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt(1)).thenReturn(1);

    billDao.saveBill(bill);

    assertEquals(1, bill.getBillId());

    verify(mockPreparedStatement, times(1)).executeUpdate();
    verify(mockPreparedStatement, times(1)).getGeneratedKeys();
    verify(mockResultSet, times(1)).next();
  }

  @Test
  public void testSaveBill_Failure_NoRowsAffected() throws Exception {
    Bill bill = new Bill(1001, BigDecimal.valueOf(500.00), BigDecimal.valueOf(600.00), BigDecimal.valueOf(100.00));
    when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);

    // Simulate no rows affected
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);

    DaoException thrown = assertThrows(DaoException.class, () -> billDao.saveBill(bill));

    assertEquals("Inserting the bill failed, no rows affected.", thrown.getMessage());

    verify(mockPreparedStatement, times(1)).executeUpdate();
    verify(mockPreparedStatement, never()).getGeneratedKeys();
  }

  @Test
  public void testGetBillsByDate_Success() throws Exception {
    LocalDate date = LocalDate.now();
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Mock multiple bills in result set
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getInt("bill_id")).thenReturn(1).thenReturn(2);
    when(mockResultSet.getInt("transaction_id")).thenReturn(1001).thenReturn(1002);
    when(mockResultSet.getDate("bill_date")).thenReturn(Date.valueOf(date));
    when(mockResultSet.getBigDecimal("total_amount")).thenReturn(BigDecimal.valueOf(500.00));
    when(mockResultSet.getBigDecimal("cash_tendered")).thenReturn(BigDecimal.valueOf(600.00));
    when(mockResultSet.getBigDecimal("change_amount")).thenReturn(BigDecimal.valueOf(100.00));

    List<Bill> bills = billDao.getBillsByDate(date);

    assertNotNull(bills);
    assertEquals(2, bills.size());

    verify(mockPreparedStatement, times(1)).setDate(1, Date.valueOf(date));
    verify(mockPreparedStatement, times(1)).executeQuery();
    verify(mockResultSet, times(3)).next();
  }

  @Test
  public void testGetBillsByDate_Failure() throws Exception {
    LocalDate date = LocalDate.now();
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);

    DaoException thrown = assertThrows(DaoException.class, () -> billDao.getBillsByDate(date));

    assertEquals("Error retrieving bills for date: " + date, thrown.getMessage());
  }
}
