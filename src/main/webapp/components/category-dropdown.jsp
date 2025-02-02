<div class="flex items-center">
    <div class="relative w-48">
        <!-- Dropdown Button -->
        <button id="dropdownButton"
                class="w-full text-left px-4 py-2 bg-green-600 text-white rounded-lg shadow-lg flex justify-between items-center focus:outline-none focus:ring-2 focus:ring-green-400">
            <span id="selectedOption">Select Category</span>
            <svg class="w-5 h-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
        </button>

        <!-- Dropdown Menu -->
        <ul id="dropdownMenu"
            class="hidden absolute w-full mt-2 bg-green-600 text-white rounded-lg shadow-lg overflow-hidden z-50 max-h-48 overflow-y-auto">
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="all">All</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="fruits">Fruits</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="vegetables">Vegetables</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="meat">Meat & Poultry</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="seafood">Seafood</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="snacks">Snacks</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="bakery">Bakery</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="dairy">Dairy</li>
            <li class="px-4 py-2 hover:bg-green-700 cursor-pointer" data-value="grains">Grains</li>
        </ul>
    </div>
</div>