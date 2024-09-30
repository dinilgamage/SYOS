package com.syos.observer;

import com.syos.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ReorderObserverTest {

  private ReorderObserver reorderObserver;
  private Inventory mockInventory;

  @BeforeEach
  public void setUp() {
    reorderObserver = new ReorderObserver();
    mockInventory = mock(Inventory.class);
  }

  @Test
  public void testReorderAlertTriggered() {
    // Arrange
    when(mockInventory.getItemCode()).thenReturn("ITEM001");

    // Act
    reorderObserver.update(mockInventory);

    // Assert
    // Verify that the reorder alert is triggered
    verify(mockInventory, times(1)).getItemCode();
  }
}
