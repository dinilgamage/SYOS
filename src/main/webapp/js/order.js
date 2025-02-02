document.addEventListener("DOMContentLoaded", function () {
    fetch('viewOrders')
        .then(response => response.json())
        .then(orders => {
            const ordersContainer = document.getElementById('orders-container');
            console.log('Orders:', orders); // Debug log to check if orders are fetched

            orders.forEach(order => {
                const orderCard = document.createElement('div');
                orderCard.className = 'bg-white p-6 rounded-lg shadow-md cursor-pointer glass-card';
                orderCard.innerHTML = `
                    <h2 class="text-xl font-semibold mb-2">Order #${order.orderId}</h2>
                    <p><strong>Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}</p>
                    <p><strong>Status:</strong> ${order.orderStatus}</p>
                    <p><strong>Total:</strong> $${order.totalAmount.toFixed(2)}</p>
                `;
                orderCard.addEventListener('click', () => openOrderDetailsModal(order));
                ordersContainer.appendChild(orderCard);
            });
        })
        .catch(error => console.error('Error fetching orders:', error)); // Debug log for errors
});

function openOrderDetailsModal(order) {
    const modal = document.getElementById('order-details-modal');
    const orderDetailsContent = document.getElementById('order-details-content');
    const orderDetailsTitle = document.getElementById('order-details-title');

    fetch(`orderDetails?orderId=${order.orderId}`)
        .then(response => response.json())
        .then(orderDetails => {
            orderDetailsTitle.textContent = `Order #${orderDetails.orderId}`;
            orderDetailsContent.innerHTML = `
                <p><strong>Date:</strong> ${new Date(orderDetails.orderDate).toLocaleDateString()}</p>
                <p><strong>Status:</strong> ${orderDetails.orderStatus}</p>
                <h4 class="text-md font-semibold mt-4">Items:</h4>
                <ul class="list-disc list-inside">
                    ${Array.isArray(orderDetails.orderItems) ? orderDetails.orderItems.map(item => `
                        <li class="flex justify-between pt-1">
                            <span>${item.itemName}</span>
                            <span>${item.quantity} x $${item.price.toFixed(2)}</span>
                        </li>
                    `).join('') : '<li>No items found</li>'}
                </ul>
                <div class="flex justify-between pt-1">
                    <strong>Total:</strong>
                    <strong>$${orderDetails.totalAmount.toFixed(2)}</strong>
                </div>
            `;
            modal.classList.remove('hidden');
            setTimeout(() => {
                modal.classList.add('visible');
            }, 10);
        })
        .catch(error => console.error('Error fetching order details:', error));
}

function closeOrderDetailsModal() {
    const modal = document.getElementById('order-details-modal');
    modal.classList.remove('visible');
    setTimeout(() => {
        modal.classList.add('hidden');
        // Reset the modal state
        modal.classList.remove('visible');
    }, 300);
}