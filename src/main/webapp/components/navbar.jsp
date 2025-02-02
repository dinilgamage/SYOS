<div class="header flex justify-between items-center p-5 bg-white shadow mb-5">
    <div class="flex items-center gap-4">
        <h2 class="text-2xl text-green-600 font-bold">SYOS</h2>
        <form action="search" method="GET" class="flex items-center">
            <input type="text" name="query" placeholder="Search..." class="border border-gray-300 rounded px-3 py-1 focus:outline-none focus:ring-2 focus:ring-green-500">
            <button type="submit" class="ml-2 text-white bg-green-500 px-3 py-1 rounded hover:bg-green-600">
                <i class="fas fa-search"></i>
            </button>
        </form>
    </div>
    <div class="flex items-center gap-4 relative">
        <button id="cart-icon" class="relative mx-4" onclick="openCartModal()">
            <i class="fas fa-shopping-cart text-2xl text-gray-700"></i>
            <span id="cart-count"
                  class="absolute -top-3 -right-3 bg-red-500 text-white text-sm rounded-full px-2 py-1">0</span>
        </button>
        <a href="viewOrders.jsp" class="relative mx-4">
            <i class="fas fa-box-open text-2xl text-gray-700"></i>
        </a>
        <div class="relative">
            <button id="profile-icon" class="relative" onclick="toggleDropdown()">
                <i class="fas fa-user-circle text-3xl text-gray-700"></i>
            </button>
            <div id="dropdown-menu" class="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded shadow-lg hidden">
                <div class="absolute top-0 right-2 w-3 h-3 bg-white border-t border-l border-gray-300 transform rotate-45 -mt-1"></div>
                <a href="logout" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">
                    <i class="fas fa-sign-out-alt mr-2"></i>Logout
                </a>
            </div>
        </div>
    </div>
</div>