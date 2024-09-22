package com.syos.dao;

import com.syos.model.BillItem;
import java.util.List;

public interface BillItemDao {
  void saveBillItem(BillItem billItem); // Saves a bill item into the database
  List<BillItem> getBillItemsByBillId(int billId); // Retrieves all bill items for a specific bill ID
}
