package com.syos.dao;

import com.syos.model.StockBatch;
import java.util.List;

public interface StockBatchDao {
  List<StockBatch> getBatchesForItem(int itemId);
  void updateBatch(StockBatch batch);
  StockBatch getNearestExpiryBatch(int itemId);
  List<StockBatch> getAllStockBatches();

}

