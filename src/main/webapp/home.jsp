

<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Home" />
</jsp:include>
<body class="font-poppins bg-gray-100">

<!-- Header Section -->
<jsp:include page="components/navbar.jsp" />

<!-- Product Cards Section -->
<div class="container mx-auto px-4 min-h-screen">
    <h3 class="text-3xl font-bold text-start mb-6">Welcome, <%= session.getAttribute("userName") %>!</h3>
    <div id="inventory-container" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <!-- Inventory items will be dynamically added here -->
    </div>
</div>

<!-- Modal Section -->
<jsp:include page="components/product-modal.jsp" />
<jsp:include page="components/cart-modal.jsp" />

<div class="footer mt-8 text-center text-sm text-gray-600">
    <p>&copy; 2024 SYOS. All rights reserved.</p>
</div>

<script src="js/productModal.js"></script>
<script src="js/cartModal.js"></script>
<script src="js/navbar.js"></script>
<script src="js/checkout.js"></script>
<script src="js/home.js"></script>

</body>