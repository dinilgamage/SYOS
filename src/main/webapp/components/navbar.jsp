<div class="header flex justify-between items-center p-5 bg-white shadow mb-5">
    <h2 class="text-2xl text-gray-800">Welcome, <%= session.getAttribute("userName") %></h2>
    <div class="flex items-center gap-4">
        <button id="cart-icon" class="relative" onclick="openCartModal()">
            <i class="fas fa-shopping-cart text-2xl text-gray-700"></i>
            <span id="cart-count"
                  class="absolute -top-2 -right-2 bg-red-500 text-white text-sm rounded-full px-2
                  py-1"><%= session.getAttribute("cartSize") %></span>
        </button>
        <a href="logout" class="text-white bg-red-500 px-3 py-2 rounded">Logout</a>
    </div>
</div>
