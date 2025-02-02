<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Home" />
</jsp:include>

<%
    // Check if the session exists and userEmail is set
    if (session == null || session.getAttribute("userEmail") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<body class="font-poppins bg-gray-100">

<jsp:include page="components/navbar.jsp" />

<!-- Product Cards Section -->
<div class="container mx-auto px-4 min-h-screen">
    <h3 class="text-3xl font-bold text-start mb-6">Welcome, <%= session.getAttribute("userName") %>!</h3>
    <div id="inventory-container" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <!-- Inventory items will be dynamically added here -->
    </div>
</div>

<!-- Notifications Section -->
<jsp:include page="components/notifications/purchase-notification.jsp" />
<jsp:include page="components/notifications/cart-notification.jsp" />
<jsp:include page="components/notifications/stock-notification.jsp" />


<!-- Modal Section -->
<jsp:include page="components/product-modal.jsp" />
<jsp:include page="components/cart-modal.jsp" />

<!-- Footer Section -->
<jsp:include page="components/footer.jsp" />

<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>
<script src="js/navbar.js"></script>
<script src="js/checkout.js"></script>
<script src="js/home.js"></script>

</body>