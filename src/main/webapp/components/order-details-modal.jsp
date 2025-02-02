<div id="order-details-modal" class="fixed inset-0 bg-black bg-opacity-70 hidden flex justify-center items-center">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg sm:max-w-[425px] relative">
        <!-- Modal Header -->
        <div class="flex items-center justify-between p-4 border-b">
            <div class="flex items-center gap-2">
                <i class="fas fa-receipt text-gray-600 text-lg"></i>
                <h3 id="order-details-title" class="text-xl font-semibold">Order Details</h3>
            </div>
            <button class="text-gray-500" onclick="closeOrderDetailsModal()">
                <i style="font-size: 25px" class="fas fa-times"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <div id="order-details-content" style="max-height: 60vh; overflow-y: auto;" class="p-6 space-y-4">
            <!-- Order details will be dynamically added here -->
        </div>

        <!-- Modal Footer -->
        <div class="p-4 border-t">
            <div class="flex justify-between items-center mb-4">
                <span class="font-semibold">Total:</span>
                <span id="order-total" class="font-semibold">$0.00</span>
            </div>
            <button onclick="reorderItems()"
                    class="bg-red-500 bg-green-500 hover:bg-green-600 text-white w-full py-2 rounded-lg">Reorder
            </button>
        </div>
    </div>
</div>