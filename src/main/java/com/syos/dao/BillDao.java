package com.syos.dao;

import com.syos.model.Bill;
import java.time.LocalDate;
import java.util.List;

public interface BillDao {
  Bill getBillById(int billId);
  void saveBill(Bill bill);
  List<Bill> getBillsByDate(LocalDate date);
}

