package com.syos.service;

import com.syos.dao.BillDao;
import com.syos.dao.BillItemDao;
import com.syos.dao.InventoryDao;
import com.syos.enums.TransactionType;
import com.syos.exception.DaoException;
import com.syos.exception.InvalidTransactionTypeException;
import com.syos.model.Bill;
import com.syos.model.BillItem;
import com.syos.model.Inventory;
import com.syos.model.OnlineTransaction;
import com.syos.model.OverTheCounterTransaction;
import com.syos.model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BillServiceTest {

  private BillDao mockBillDao;
  private BillItemDao mockBillItemDao;
  private TransactionService mockTransactionService;
  private InventoryService mockInventoryService;
  private InventoryDao mockInventoryDao;

  private BillService billService;

  @BeforeEach
  public void setUp() {
    mockBillDao = mock(BillDao.class);
    mockBillItemDao = mock(BillItemDao.class);
    mockTransactionService = mock(TransactionService.class);
    mockInventoryService = mock(InventoryService.class);
    mockInventoryDao = mock(InventoryDao.class);

    billService = new BillService(mockBillDao, mockBillItemDao, mockTransactionService, mockInventoryService, mockInventoryDao);
  }

  @AfterEach
  public void tearDown() {
    // No teardown needed as i am are not using any static mocks
  }

  /**
   * Helper method to create a sample BillItem.
   */
  private BillItem createBillItem(String itemCode, int quantity, BigDecimal itemPrice) {
    return new BillItem(itemCode, quantity, itemPrice);
  }

  /**
   * Helper method to create a sample Inventory.
   */
  private Inventory createInventory(int itemId, String itemCode, String name, BigDecimal price,
    int storeStock, int onlineStock, int shelfCapacity) {
    Inventory inventory = new Inventory(itemCode, name, price, storeStock, onlineStock, shelfCapacity);
    inventory.setItemId(itemId);
    return inventory;
  }

  /**
   * Helper method to create a sample Transaction.
   */
  private Transaction createTransaction(int transactionId, TransactionType type, BigDecimal totalAmount, Integer userId) {
    Transaction transaction;
    if (userId != null) {
      transaction = new OnlineTransaction(totalAmount, userId);
    } else {
      transaction = new OverTheCounterTransaction(totalAmount);
    }
    transaction.setTransactionId(transactionId);
    return transaction;
  }

  /**
   * Happy Path: Successfully build and save an online bill.
   */
  @Test
  public void testBuildBill_Online_Success() throws Exception {
    // Arrange
    BillItem item1 = createBillItem("ITEM001", 2, new BigDecimal("50.00"));
    BillItem item2 = createBillItem("ITEM002", 1, new BigDecimal("30.00"));
    List<BillItem> items = Arrays.asList(item1, item2);
    TransactionType transactionType = TransactionType.ONLINE;
    Integer userId = 1001;

    BigDecimal transactionTotal = new BigDecimal("130.00"); // (2*50) + (1*30)

    Transaction transaction = createTransaction(5001, transactionType, transactionTotal, userId);

    when(mockTransactionService.createTransaction(transactionType, transactionTotal, userId)).thenReturn(transaction);

    Inventory inventory1 = createInventory(1, "ITEM001", "Item One", new BigDecimal("50.00"), 10, 5, 20);
    Inventory inventory2 = createInventory(2, "ITEM002", "Item Two", new BigDecimal("30.00"), 15, 10, 25);

    when(mockInventoryDao.getItemByCode("ITEM001")).thenReturn(inventory1);
    when(mockInventoryDao.getItemByCode("ITEM002")).thenReturn(inventory2);

    // Act
    Bill bill = billService.buildBill(items, transactionType, userId);

    // Assert
    assertNotNull(bill);
    assertEquals(transaction.getTransactionId(), bill.getTransactionId());
    assertEquals(new BigDecimal("130.00"), bill.getTotalAmount());
    assertNull(bill.getCashTendered());
    assertNull(bill.getChangeAmount());
    assertEquals(2, bill.getBillItems().size());

    // Verify interactions
    verify(mockTransactionService, times(1)).createTransaction(transactionType, transactionTotal, userId);
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM001");
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM002");
    verify(mockInventoryService, times(1)).updateInventoryStock("ITEM001", 2, transactionType);
    verify(mockInventoryService, times(1)).updateInventoryStock("ITEM002", 1, transactionType);
    verify(mockBillDao, times(1)).saveBill(bill);
    verify(mockBillItemDao, times(2)).saveBillItem(any(BillItem.class));
  }

  /**
   * Happy Path: Successfully build and save an in-store bill with cash tendered.
   */
  @Test
  public void testBuildBill_Store_Success() throws Exception {
    // Arrange
    BillItem item1 = createBillItem("ITEM003", 3, new BigDecimal("20.00"));
    List<BillItem> items = Arrays.asList(item1);
    TransactionType transactionType = TransactionType.STORE;
    BigDecimal cashTendered = new BigDecimal("70.00");

    BigDecimal transactionTotal = new BigDecimal("60.00"); // 3*20

    Transaction transaction = createTransaction(5002, transactionType, transactionTotal, null);

    when(mockTransactionService.createTransaction(transactionType, transactionTotal, null)).thenReturn(transaction);

    Inventory inventory1 = createInventory(3, "ITEM003", "Item Three", new BigDecimal("20.00"), 8, 4, 15);

    when(mockInventoryDao.getItemByCode("ITEM003")).thenReturn(inventory1);

    // Act
    Bill bill = billService.buildBill(items, transactionType, cashTendered);

    // Assert
    assertNotNull(bill);
    assertEquals(transaction.getTransactionId(), bill.getTransactionId());
    assertEquals(new BigDecimal("60.00"), bill.getTotalAmount());
    assertEquals(new BigDecimal("70.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("10.00"), bill.getChangeAmount());
    assertEquals(1, bill.getBillItems().size());

    // Verify interactions
    verify(mockTransactionService, times(1)).createTransaction(transactionType, transactionTotal, null);
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM003");
    verify(mockInventoryService, times(1)).updateInventoryStock("ITEM003", 3, transactionType);
    verify(mockBillDao, times(1)).saveBill(bill);
    verify(mockBillItemDao, times(1)).saveBillItem(any(BillItem.class));
  }

  /**
   * Edge Case: Attempt to build a bill with an empty item list.
   */
  @Test
  public void testBuildBill_EmptyItems() {
    // Arrange
    List<BillItem> items = Collections.emptyList(); // Empty list
    TransactionType transactionType = TransactionType.STORE;
    BigDecimal cashTendered = new BigDecimal("50.00");

    // Act & Assert
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      billService.buildBill(items, transactionType, cashTendered);
    });

    assertEquals("Bill must contain at least one item", thrown.getMessage());

    // Verify no interactions
    verify(mockTransactionService, never()).createTransaction(any(), any(), any());
    verify(mockInventoryDao, never()).getItemByCode(anyString());
    verify(mockInventoryService, never()).updateInventoryStock(anyString(), anyInt(), any());
    verify(mockBillDao, never()).saveBill(any(Bill.class));
    verify(mockBillItemDao, never()).saveBillItem(any(BillItem.class));
  }

  /**
   * Edge Case: Attempt to build a bill with a null item list.
   */
  @Test
  public void testBuildBill_NullItems() {
    // Arrange
    List<BillItem> items = null;
    TransactionType transactionType = TransactionType.STORE;
    BigDecimal cashTendered = new BigDecimal("50.00");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      billService.buildBill(items, transactionType, cashTendered);
    });

    // Verify no interactions
    verify(mockTransactionService, never()).createTransaction(any(), any(), any());
    verify(mockInventoryDao, never()).getItemByCode(anyString());
    verify(mockInventoryService, never()).updateInventoryStock(anyString(), anyInt(), any());
    verify(mockBillDao, never()).saveBill(any(Bill.class));
    verify(mockBillItemDao, never()).saveBillItem(any(BillItem.class));
  }

  /**
   * Edge Case: Exception thrown while saving the bill.
   */
  @Test
  public void testBuildBill_SaveBillException() throws Exception {
    // Arrange
    BillItem item1 = createBillItem("ITEM005", 2, new BigDecimal("25.00"));
    List<BillItem> items = Arrays.asList(item1);
    TransactionType transactionType = TransactionType.ONLINE;
    Integer userId = 1003;

    BigDecimal transactionTotal = new BigDecimal("50.00"); // 2*25

    Transaction transaction = createTransaction(5005, transactionType, transactionTotal, userId);

    when(mockTransactionService.createTransaction(transactionType, transactionTotal, userId)).thenReturn(transaction);

    Inventory inventory1 = createInventory(5, "ITEM005", "Item Five", new BigDecimal("25.00"), 10, 5, 20);

    when(mockInventoryDao.getItemByCode("ITEM005")).thenReturn(inventory1);

    // Simulate updateInventoryStock working fine
    doNothing().when(mockInventoryService).updateInventoryStock("ITEM005", 2, transactionType);

    // Simulate saveBill throwing an exception
    doThrow(new DaoException("Database error while saving bill"))
      .when(mockBillDao).saveBill(any(Bill.class));

    // Act & Assert
    DaoException thrown = assertThrows(DaoException.class, () -> {
      billService.buildBill(items, transactionType, userId);
    });

    assertEquals("Database error while saving bill", thrown.getMessage());

    // Verify interactions
    verify(mockTransactionService, times(1)).createTransaction(transactionType, transactionTotal, userId);
    verify(mockInventoryDao, times(1)).getItemByCode("ITEM005");
    verify(mockInventoryService, times(1)).updateInventoryStock("ITEM005", 2, transactionType);
    verify(mockBillDao, times(1)).saveBill(any(Bill.class));
    verify(mockBillItemDao, never()).saveBillItem(any(BillItem.class));
  }

  /**
   * Edge Case: Attempt to build a bill with null TransactionType.
   */
  @Test
  public void testBuildBill_NullTransactionType() {
    // Arrange
    BillItem item1 = createBillItem("ITEM006", 1, new BigDecimal("15.00"));
    List<BillItem> items = Arrays.asList(item1);
    TransactionType transactionType = null;
    Integer userId = 1004;

    // Act & Assert
    assertThrows(InvalidTransactionTypeException.class, () -> {
      billService.buildBill(items, transactionType, userId);
    });

    // Verify no interactions
    verify(mockTransactionService, never()).createTransaction(any(), any(), any());
    verify(mockInventoryDao, never()).getItemByCode(anyString());
    verify(mockInventoryService, never()).updateInventoryStock(anyString(), anyInt(), any());
    verify(mockBillDao, never()).saveBill(any(Bill.class));
    verify(mockBillItemDao, never()).saveBillItem(any(BillItem.class));
  }

  /**
   * Edge Case: Cash tendered is less than the total amount in store transactions.
   */
  @Test
  public void testBuildBill_Store_CashTenderedLessThanTotal_ShouldThrowException() throws Exception {
    // Arrange
    BillItem item1 = createBillItem("ITEM007", 2, new BigDecimal("40.00"));
    List<BillItem> items = Arrays.asList(item1);
    TransactionType transactionType = TransactionType.STORE;
    BigDecimal cashTendered = new BigDecimal("70.00"); // Less than the total of 80.00
    BigDecimal transactionTotal = new BigDecimal("80.00");

    when(mockTransactionService.createTransaction(transactionType, transactionTotal, null)).thenReturn(createTransaction(5006, transactionType, transactionTotal, null));
    when(mockInventoryDao.getItemByCode("ITEM007")).thenReturn(createInventory(7, "ITEM007", "Item Seven", new BigDecimal("40.00"), 10, 5, 20));
    doNothing().when(mockInventoryService).updateInventoryStock("ITEM007", 2, transactionType);

    // Act and Assert
    assertThrows(IllegalArgumentException.class, () -> {
      billService.buildBill(items, transactionType, cashTendered); // This should throw an exception
    });

    // Verify that the transaction was not created
    verify(mockTransactionService, never()).createTransaction(any(), any(), any());
  }

}
