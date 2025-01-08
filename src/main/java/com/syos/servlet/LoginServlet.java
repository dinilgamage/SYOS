package com.syos.servlet;

import com.syos.dao.impl.UserDaoImpl;
import com.syos.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
  private UserService userService;

  @Override
  public void init() throws ServletException {
    this.userService = new UserService(new UserDaoImpl());
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    boolean isAuthenticated = userService.loginUser(email, password);

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
