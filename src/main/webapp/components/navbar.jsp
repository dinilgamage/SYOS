<div class="header flex justify-between items-center p-5 bg-white shadow mb-5">
    <h2 class="text-2xl text-gray-800">Welcome, <%= session.getAttribute("userName") %></h2>
    <a href="logout" class="text-white bg-red-500 px-3 py-2 rounded">Logout</a>
</div>