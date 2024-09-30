package com.syos.command;

import com.syos.enums.TransactionType;
import com.syos.model.BillItem;
import com.syos.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GenerateBillCommandTest {

  @Mock
  private BillService mockBillService;

  @Mock
  private List<BillItem> mockBillItems;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testExecute_StoreTransaction() {
    // Arrange
    BigDecimal cashTendered = new BigDecimal("100.00");
    GenerateBillCommand command = new GenerateBillCommand(mockBillService, mockBillItems, TransactionType.STORE, cashTendered);

    // Act
    command.execute();

    // Assert
    verify(mockBillService, times(1)).buildBill(eq(mockBillItems), eq(TransactionType.STORE), eq(cashTendered));
    verify(mockBillService, never()).buildBill(anyList(), any(TransactionType.class), anyInt());
  }

  @Test
  public void testExecute_OnlineTransaction() {
    // Arrange
    Integer userId = 123;
    GenerateBillCommand command = new GenerateBillCommand(mockBillService, mockBillItems, TransactionType.ONLINE, userId);

    // Act
    command.execute();

    // Assert
    verify(mockBillService, times(1)).buildBill(eq(mockBillItems), eq(TransactionType.ONLINE), eq(userId));
    verify(mockBillService, never()).buildBill(anyList(), any(TransactionType.class), any(BigDecimal.class));
  }

  @Test
  public void testExecute_OnlineTransactionWithoutUserId_ThrowsException() {
    // Arrange
    GenerateBillCommand command = new GenerateBillCommand(mockBillService, mockBillItems, TransactionType.ONLINE, (Integer) null);

    // Act & Assert
    try {
      command.execute();
    } catch (IllegalArgumentException ex) {
      // Verify exception message
      assertEquals("User ID is required for online transactions.", ex.getMessage());
    }

    // Ensure buildBill with cashTendered was never called
    verify(mockBillService, never()).buildBill(anyList(), any(TransactionType.class), any(BigDecimal.class));
  }

  @Test
  public void testExecute_StoreTransactionWithoutCashTendered_ThrowsException() {
    // Arrange
    GenerateBillCommand command = new GenerateBillCommand(mockBillService, mockBillItems, TransactionType.STORE, (BigDecimal) null);

    // Act & Assert
    try {
      command.execute();
    } catch (IllegalArgumentException ex) {
      // Verify exception message
      assertEquals("Invalid transaction type: STORE", ex.getMessage());
    }

    // Ensure buildBill with userId was never called
    verify(mockBillService, never()).buildBill(anyList(), any(TransactionType.class), anyInt());
  }
}
