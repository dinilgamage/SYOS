document.addEventListener("DOMContentLoaded", function () {
    fetch('viewOrders')
        .then(response => response.json())
        .then(orders => {
            const ordersContainer = document.getElementById('orders-container');
            orders.forEach(order => {
                const orderCard = document.createElement('div');
                orderCard.className = 'bg-white p-6 rounded-lg shadow-md';
                orderCard.innerHTML = `
                            <h2 class="text-xl font-semibold mb-2">Order #${order.orderId}</h2>
                            <p><strong>Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}</p>
                            <p><strong>Status:</strong> ${order.orderStatus}</p>
                            <p><strong>Total:</strong> $${order.totalAmount.toFixed(2)}</p>
                            <h3 class="text-lg font-semibold mt-4">Items:</h3>
                            <ul class="list-disc list-inside">
                                ${order.orderItems.map(item => `
                                    <li>${item.productName} - ${item.quantity} x $${item.price.toFixed(2)}</li>
                                `).join('')}
                            </ul>
                        `;
                ordersContainer.appendChild(orderCard);
            });
        });
});