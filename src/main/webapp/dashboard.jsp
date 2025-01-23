<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.syos.model.Inventory" %>

<%
    // Check if the session exists and userEmail is set
    if (session == null || session.getAttribute("userEmail") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Check if inventory items are present
    if (request.getAttribute("inventoryItems") == null) {
        response.sendRedirect("dashboard");
        return;
    }
%>

<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Dashboard" />
</jsp:include>
<body class="font-poppins bg-gray-100">

<!-- Header Section -->
<jsp:include page="components/navbar.jsp" />

<!-- Product Cards Section -->
<div class="container mx-auto px-4">
    <h3 class="text-xl mb-3">Inventory Items</h3>
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <%
            List<Inventory> inventoryItems = (List<Inventory>) request.getAttribute("inventoryItems");

            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                for (Inventory item : inventoryItems) {
        %>
        <div class="glass-card bg-white p-4 rounded-xl shadow-lg hover:scale-105 transition-transform cursor-pointer"
             onclick="openProductModal('<%= item.getItemCode() %>', '<%= item.getName() %>',
                     '<%= item.getPrice() %>', '<%= item.getDiscountValue() %>', '<%= item.getDesc() %>')">
            <img src="images/products/<%= item.getItemCode() %>.jpg" alt="<%= item.getName() %>" class="w-full h-48 object-cover rounded-lg mb-4">
            <h4 class="font-semibold text-lg mb-2"><%= item.getName() %></h4>
            <p class="text-primary font-medium text-lg">$<%= item.getPrice() %></p>
        </div>
        <%
            }
        } else {
        %>
        <p class="col-span-full text-center text-gray-700">No inventory items available.</p>
        <%
            }
        %>
    </div>
</div>

<!-- Modal Section -->
<jsp:include page="components/product-modal.jsp" />
<jsp:include page="components/cart-modal.jsp" />


<div class="footer mt-8 text-center text-sm text-gray-600">
    <p>&copy; 2024 SYOS. All rights reserved.</p>
</div>

<!-- JavaScript for Modal and Cart -->
<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>

</body>
</html>
