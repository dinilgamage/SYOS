package com.syos.dao;

import com.syos.model.StockBatch;
import java.util.List;

public interface StockBatchDao {
  List<StockBatch> getBatchesForItem(int itemId); // Retrieves batches for a specific inventory item
  void updateBatch(StockBatch batch); // Updates the stock information for a batch
  StockBatch getNearestExpiryBatch(int itemId); // Retrieves the batch that is closest to its expiration date
  List<StockBatch> getAllStockBatches(); // Fetches all stock batches in the system.

}

