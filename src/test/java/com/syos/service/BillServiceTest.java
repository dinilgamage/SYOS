//package com.syos.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.syos.dao.BillDao;
//import com.syos.dao.BillItemDao;
//import com.syos.dao.InventoryDao;
//import com.syos.model.Bill;
//import com.syos.model.BillItem;
//import com.syos.model.Inventory;
//import com.syos.model.Transaction;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//
//public class BillServiceTest {
//
//  private BillService billService;
//  private BillDao billDao;
//  private BillItemDao billItemDao;
//  private InventoryDao inventoryDao;
//  private TransactionService transactionService;
//  private InventoryService inventoryService;
//
//  @BeforeEach
//  public void setUp() {
//    billDao = mock(BillDao.class);
//    billItemDao = mock(BillItemDao.class);
//    inventoryDao = mock(InventoryDao.class);
//    transactionService = mock(TransactionService.class);
//    inventoryService = mock(InventoryService.class);
//
//    billService = new BillService(billDao, billItemDao, transactionService, inventoryService, inventoryDao);
//  }
//
//  @Test
//  public void testBuildBill_OnlineTransaction() {
//    // Arrange
//    List<BillItem> items = Arrays.asList(
//      new BillItem("item001", 2, new BigDecimal("100.00")),
//      new BillItem("item002", 1, new BigDecimal("200.00"))
//                                        );
//    String transactionType = "online";
//    Integer userId = 123;
//
//    Transaction mockTransaction = mock(Transaction.class);
//    when(mockTransaction.getTransactionId()).thenReturn(1);
//    when(transactionService.createTransaction(eq(transactionType), any(BigDecimal.class), eq(userId)))
//      .thenReturn(mockTransaction);
//
//    when(inventoryDao.getItemByCode("item001")).thenReturn(new Inventory("item001", "Item 1", new BigDecimal("100.00"), 10, 5, 20));
//    when(inventoryDao.getItemByCode("item002")).thenReturn(new Inventory("item002", "Item 2", new BigDecimal("200.00"), 10, 5, 20));
//
//    // Act
//    Bill bill = billService.buildBill(items, transactionType, null, userId);
//
//    // Assert
//    assertNotNull(bill);
//    assertEquals(2, bill.getBillItems().size());
//    verify(inventoryService, times(1)).updateInventoryStock("item001", 2, transactionType);
//    verify(inventoryService, times(1)).updateInventoryStock("item002", 1, transactionType);
//    verify(billDao, times(1)).saveBill(bill);
//    verify(billItemDao, times(2)).saveBillItem(any(BillItem.class));
//  }
//
//  @Test
//  public void testBuildBill_InStoreTransaction_WithChange() {
//    // Arrange
//    List<BillItem> items = Arrays.asList(
//      new BillItem("item001", 3, new BigDecimal("50.00")),
//      new BillItem("item002", 2, new BigDecimal("75.00"))
//                                        );
//    String transactionType = "over-the-counter";
//    BigDecimal cashTendered = new BigDecimal("500.00");
//
//    Transaction mockTransaction = mock(Transaction.class);
//    when(mockTransaction.getTransactionId()).thenReturn(2);
//    when(transactionService.createTransaction(eq(transactionType), any(BigDecimal.class), eq(null)))
//      .thenReturn(mockTransaction);
//
//    when(inventoryDao.getItemByCode("item001")).thenReturn(new Inventory("item001", "Item 1", new BigDecimal("50.00"), 10, 5, 20));
//    when(inventoryDao.getItemByCode("item002")).thenReturn(new Inventory("item002", "Item 2", new BigDecimal("75.00"), 10, 5, 20));
//
//    // Act
//    Bill bill = billService.buildBill(items, transactionType, cashTendered, null);
//
//    // Assert
//    assertNotNull(bill);
//    assertEquals(2, bill.getBillItems().size());
//    assertEquals(new BigDecimal("500.00"), bill.getCashTendered());
//    assertEquals(new BigDecimal("125.00"), bill.getChangeAmount());  // 500 - (3*50 + 2*75)
//    verify(inventoryService, times(1)).updateInventoryStock("item001", 3, transactionType);
//    verify(inventoryService, times(1)).updateInventoryStock("item002", 2, transactionType);
//    verify(billDao, times(1)).saveBill(bill);
//    verify(billItemDao, times(2)).saveBillItem(any(BillItem.class));
//  }
//
//  @Test
//  public void testBuildBill_NoItems() {
//    // Arrange
//    List<BillItem> items = Arrays.asList();
//    String transactionType = "online";
//    Integer userId = 123;
//
//    Transaction mockTransaction = mock(Transaction.class);
//    when(mockTransaction.getTransactionId()).thenReturn(3);
//    when(transactionService.createTransaction(eq(transactionType), any(BigDecimal.class), eq(userId)))
//      .thenReturn(mockTransaction);
//
//    // Act
//    Bill bill = billService.buildBill(items, transactionType, null, userId);
//
//    // Assert
//    assertNotNull(bill);
//    assertEquals(0, bill.getBillItems().size());
//    verify(inventoryService, never()).updateInventoryStock(anyString(), anyInt(), anyString());
//    verify(billDao, times(1)).saveBill(bill);
//    verify(billItemDao, never()).saveBillItem(any(BillItem.class));
//  }
//
//  @Test
//  public void testCalculateTransactionTotal() {
//    // Arrange
//    List<BillItem> items = Arrays.asList(
//      new BillItem("item001", 2, new BigDecimal("100.00")),
//      new BillItem("item002", 1, new BigDecimal("200.00"))
//                                        );
//
//    // Act
//    BigDecimal total = billService.calculateTransactionTotal(items);
//
//    // Assert
//    assertEquals(new BigDecimal("400.00"), total);  // (2*100) + (1*200)
//  }
//
//  @Test
//  public void testBuildBill_InStoreTransaction_CashTenderedLessThanTotal_ThrowsException() {
//    // Arrange
//    List<BillItem> items = Arrays.asList(
//      new BillItem("item001", 3, new BigDecimal("50.00")),
//      new BillItem("item002", 2, new BigDecimal("75.00"))
//                                        );
//    String transactionType = "over-the-counter";
//    BigDecimal cashTendered = new BigDecimal("200.00");  // Less than total amount (375.00)
//
//    Transaction mockTransaction = mock(Transaction.class);
//    when(mockTransaction.getTransactionId()).thenReturn(2);
//
//    when(inventoryDao.getItemByCode("item001")).thenReturn(new Inventory("item001", "Item 1", new BigDecimal("50.00"), 10, 5, 20));
//    when(inventoryDao.getItemByCode("item002")).thenReturn(new Inventory("item002", "Item 2", new BigDecimal("75.00"), 10, 5, 20));
//
//    // Act & Assert
//    assertThrows(IllegalArgumentException.class, () -> {
//      billService.buildBill(items, transactionType, cashTendered, null);
//    });
//  }
//
//}
