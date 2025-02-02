<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="components/head.jsp">
    <jsp:param name="title" value="Login" />
</jsp:include>
<body class="bg-gray-100 flex items-center justify-center h-screen p-4">
<div class="bg-white p-8 rounded shadow-md w-full max-w-sm">
    <h2 class="text-2xl font-bold mb-6 text-center">Login</h2>
    <form action="login" method="post" class="space-y-4">
        <div>
            <label class="block text-gray-700">Email:</label>
            <input type="email" name="email" required class="w-full px-3 py-2 border rounded">
        </div>
        <div>
            <label class="block text-gray-700">Password:</label>
            <input type="password" name="password" required class="w-full px-3 py-2 border rounded">
        </div>
        <div>
            <input type="submit" value="Login" class="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600">
        </div>
    </form>
    <% if (request.getAttribute("error") != null) { %>
    <p class="mt-4 text-red-500 text-center"><%= request.getAttribute("error") %></p>
    <% } %>
    <p class="mt-4 text-center">Don't have an account? <a href="register.jsp" class="text-blue-500 hover:underline">Register here</a></p>
</div>
<script src="js/home.js"></script>
</body>