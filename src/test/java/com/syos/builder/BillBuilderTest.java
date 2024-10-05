package com.syos.builder;

import com.syos.model.Bill;
import com.syos.model.BillItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BillBuilderTest {

  private BillBuilder billBuilder;
  private BillItem item1;
  private BillItem item2;

  @BeforeEach
  public void setUp() {
    // Common setup for each test
    billBuilder = new BillBuilder();
    item1 = new BillItem("ITEM001", 2, new BigDecimal("50.00"));
    item2 = new BillItem("ITEM002", 1, new BigDecimal("100.00"));
  }

  @Test
  public void testBuildBillWithSingleItem() {
    // Act
    Bill bill = billBuilder.addItem(item1)
      .setTransactionId(1)
      .setCashTendered(new BigDecimal("120.00"))
      .setChangeAmount(new BigDecimal("20.00"))
      .build();

    // Assert
    assertEquals(1, bill.getTransactionId());
    assertEquals(new BigDecimal("100.00"), bill.getTotalAmount());
    assertEquals(new BigDecimal("120.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("20.00"), bill.getChangeAmount());
    assertEquals(1, bill.getBillItems().size());
    assertEquals(item1, bill.getBillItems().get(0));
  }

  @Test
  public void testBuildBillWithMultipleItems() {
    // Act
    Bill bill = billBuilder.addItem(item1)
      .addItem(item2)
      .setTransactionId(2)
      .setCashTendered(new BigDecimal("200.00"))
      .setChangeAmount(new BigDecimal("50.00"))
      .build();

    // Assert
    assertEquals(2, bill.getTransactionId());
    assertEquals(new BigDecimal("200.00"), bill.getTotalAmount());
    assertEquals(new BigDecimal("200.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("50.00"), bill.getChangeAmount());
    assertEquals(2, bill.getBillItems().size());
    assertEquals(item1, bill.getBillItems().get(0));
    assertEquals(item2, bill.getBillItems().get(1));
  }

  @Test
  public void testBuildBillWithoutItems() {
    // Act
    Bill bill = billBuilder.setTransactionId(3)
      .setCashTendered(new BigDecimal("100.00"))
      .setChangeAmount(new BigDecimal("100.00"))
      .build();

    // Assert
    assertEquals(3, bill.getTransactionId());
    assertEquals(BigDecimal.ZERO, bill.getTotalAmount());
    assertEquals(new BigDecimal("100.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("100.00"), bill.getChangeAmount());
    assertEquals(0, bill.getBillItems().size());
  }

  @Test
  public void testAddMultipleItemsToBill() {
    // Act
    Bill bill = billBuilder.addItem(item1)
      .addItem(item2)
      .setTransactionId(4)
      .build();

    // Assert
    assertEquals(4, bill.getTransactionId());
    assertEquals(new BigDecimal("200.00"), bill.getTotalAmount());
    assertEquals(2, bill.getBillItems().size());
    assertEquals(item1, bill.getBillItems().get(0));
    assertEquals(item2, bill.getBillItems().get(1));
  }

  @Test
  public void testBillChangeAmount() {
    // Act
    Bill bill = billBuilder.addItem(item1)
      .setTransactionId(5)
      .setCashTendered(new BigDecimal("150.00"))
      .setChangeAmount(new BigDecimal("50.00"))
      .build();

    // Assert
    assertEquals(5, bill.getTransactionId());
    assertEquals(new BigDecimal("100.00"), bill.getTotalAmount());
    assertEquals(new BigDecimal("150.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("50.00"), bill.getChangeAmount());
  }

  @Test
  public void testNoItemsAdded() {
    // Act
    Bill bill = billBuilder.setTransactionId(6)
      .setCashTendered(new BigDecimal("50.00"))
      .setChangeAmount(new BigDecimal("50.00"))
      .build();

    // Assert
    assertEquals(6, bill.getTransactionId());
    assertEquals(BigDecimal.ZERO, bill.getTotalAmount());
    assertEquals(new BigDecimal("50.00"), bill.getCashTendered());
    assertEquals(new BigDecimal("50.00"), bill.getChangeAmount());
    assertTrue(bill.getBillItems().isEmpty());
  }
}
