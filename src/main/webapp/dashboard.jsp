<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard</title>
</head>
<body>
    <h2>Welcome, <%= session.getAttribute("userEmail") %></h2>
    <a href="logout">Logout</a>
</body>
</html>
