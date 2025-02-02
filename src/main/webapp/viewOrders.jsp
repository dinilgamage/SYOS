<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="View Orders" />
</jsp:include>

<%
    // Check if the session exists and userEmail is set
    if (session == null || session.getAttribute("userEmail") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<body class="bg-gray-100 text-gray-900">

<jsp:include page="components/navbar.jsp" />

<div class="container mx-auto px-4">
    <h1 class="text-3xl font-bold text-start mb-6">Your Orders</h1>
    <div id="orders-container" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- Orders will be dynamically added here -->
    </div>
</div>

<jsp:include page="components/cart-modal.jsp" />

<script src="js/order.js"></script>
<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>
<script src="js/navbar.js"></script>
<script src="js/checkout.js"></script>

</body>