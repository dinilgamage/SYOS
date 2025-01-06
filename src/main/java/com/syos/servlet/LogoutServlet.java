package com.syos.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    HttpSession session = request.getSession(false); // Avoid creating a new session
    if (session != null) {
      session.invalidate(); // Destroy the session
    }
    response.sendRedirect("login.jsp");
  }
}
