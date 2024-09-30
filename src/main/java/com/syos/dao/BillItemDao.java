package com.syos.dao;

import com.syos.model.BillItem;
import java.util.List;

public interface BillItemDao {
  void saveBillItem(BillItem billItem);
  List<BillItem> getBillItemsByBillId(int billId);
}
