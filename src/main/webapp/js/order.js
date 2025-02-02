document.addEventListener("DOMContentLoaded", function () {
    fetch('viewOrders')
        .then(response => response.json())
        .then(orders => {
            const ordersContainer = document.getElementById('orders-container');
            console.log('Orders:', orders); // Debug log to check if orders are fetched

            orders.forEach(order => {
                const orderCard = document.createElement('div');
                orderCard.className = 'bg-white p-6 rounded-lg shadow-md';
                orderCard.innerHTML = `
                    <h2 class="text-xl font-semibold mb-2">Order #${order.orderId}</h2>
                    <p><strong>Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}</p>
                    <p><strong>Status:</strong> ${order.orderStatus}</p>
                    <p><strong>Total:</strong> $${order.totalAmount.toFixed(2)}</p>
                `;
                ordersContainer.appendChild(orderCard);
            });
        })
        .catch(error => console.error('Error fetching orders:', error)); // Debug log for errors
});