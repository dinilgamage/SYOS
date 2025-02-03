<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Register" />
</jsp:include>
<body class="bg-gray-100 flex items-center justify-center h-screen p-4">
<div class="bg-white flex w-full max-w-4xl rounded shadow-md">
    <!-- Image Section -->
    <div class="w-1/2">
        <img src="images/register.jpg" alt="Register Image" class="w-full h-full object-cover rounded-l">
    </div>
    <!-- Register Card -->
    <div class="w-1/2 p-8 flex flex-col justify-center">
        <h2 class="text-3xl font-bold mb-10 text-center text-green-600">Sign up for SYOS!</h2>
        <h2 class="text-2xl font-bold mb-6 text-center">Register</h2>
        <form name="registerForm" action="register" method="post" class="space-y-4" onsubmit="return validateForm()">
            <div>
                <input type="text" name="name" placeholder="Name" required class="w-full px-3 py-2 border rounded">
            </div>
            <div>
                <input type="email" name="email" placeholder="Email" required class="w-full px-3 py-2 border rounded">
            </div>
            <div>
                <input type="password" name="password" placeholder="Password" required class="w-full px-3 py-2 border rounded">
            </div>
            <div id="error-message" class="text-red-500 text-center"></div>
            <% if (request.getAttribute("error") != null) { %>
            <p class="mt-4 text-red-500 text-center"><%= request.getAttribute("error") %></p>
            <% } %>
            <div class="pt-4">
                <input type="submit" value="Register" class="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600 cursor-pointer">
            </div>
        </form>
        <p class="mt-4 text-center">Already have an account? <a href="login.jsp" class="text-green-500 hover:underline">Login here</a></p>
    </div>
</div>
<script src="js/home.js"></script>
<script src="js/register.js"></script>
</body>