package com.syos.dao;

import com.syos.model.Bill;
import java.time.LocalDate;
import java.util.List;

public interface BillDao {
  Bill getBillById(int billId);  // Retrieves a bill by its ID
  void saveBill(Bill bill);      // Saves a new bill into the database
  List<Bill> getBillsByDate(LocalDate date);  // Retrieves bills generated on a specific date
}

