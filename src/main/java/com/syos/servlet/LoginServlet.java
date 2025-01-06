package com.syos.servlet;

import com.syos.dao.impl.BillDaoImpl;
import com.syos.dao.impl.BillItemDaoImpl;
import com.syos.dao.impl.InventoryDaoImpl;
import com.syos.dao.impl.StockBatchDaoImpl;
import com.syos.dao.impl.TransactionDaoImpl;
import com.syos.dao.impl.UserDaoImpl;
import com.syos.facade.StoreFacade;
import com.syos.facade.StoreFacadeImpl;
import com.syos.service.BillService;
import com.syos.service.DiscountService;
import com.syos.service.InventoryService;
import com.syos.service.ReportService;
import com.syos.service.TransactionService;
import com.syos.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
  private StoreFacade storeFacade;

  @Override
  public void init() throws ServletException {
    storeFacade = new StoreFacadeImpl(
      new InventoryService(new InventoryDaoImpl(), new StockBatchDaoImpl()),
      new BillService(new BillDaoImpl(), new BillItemDaoImpl(),
        new TransactionService(new TransactionDaoImpl()),
        new InventoryService(new InventoryDaoImpl(), new StockBatchDaoImpl()),
        new InventoryDaoImpl()
      ),
      new ReportService(new InventoryDaoImpl(), new TransactionDaoImpl(), new StockBatchDaoImpl()),
      new InventoryDaoImpl(),
      new DiscountService(),
      new UserService(new UserDaoImpl())
    );
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    boolean isAuthenticated = storeFacade.loginUser(email, password);

    if (isAuthenticated) {
      HttpSession session = request.getSession();
      session.setAttribute("userEmail", email);
      response.sendRedirect("dashboard.jsp");
    } else {
      request.setAttribute("error", "Invalid credentials. Please try again.");
      request.getRequestDispatcher("login.jsp").forward(request, response);
    }
  }
}
