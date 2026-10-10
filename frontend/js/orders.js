/**
 * RUTHRAMART - ORDER & CHECKOUT MANAGEMENT
 */

const ordersManager = {
    async initCheckout() {
        if (!auth.requireAuth(['BUYER', 'ADMIN'])) return;

        const user = auth.getUser();
        if (user) {
            const nameField = document.getElementById('checkout-name');
            const emailField = document.getElementById('checkout-email');
            const phoneField = document.getElementById('checkout-phone');

            if (nameField) nameField.value = user.name || '';
            if (emailField) emailField.value = user.email || '';
            if (phoneField) phoneField.value = user.phone || '';
        }

        await this.loadCheckoutSummary();
    },

    async loadCheckoutSummary() {
        const summaryList = document.getElementById('checkout-items-list');
        const totalAmountEl = document.getElementById('checkout-total-amount');

        try {
            const cartData = await api.get('/cart');
            if (!cartData.items || cartData.items.length === 0) {
                alert('Your cart is empty. Please add items before checking out.');
                window.location.href = '/products.html';
                return;
            }

            if (summaryList) {
                summaryList.innerHTML = cartData.items.map(item => `
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 0.95rem;">
                        <div>
                            <strong>${item.name}</strong> 
                            <span style="color: var(--text-secondary); margin-left: 6px;">× ${item.quantity}</span>
                        </div>
                        <div style="font-weight: 700; color: var(--secondary);">
                            ${api.formatCurrency(item.subtotal)}
                        </div>
                    </div>
                `).join('');
            }

            if (totalAmountEl) {
                totalAmountEl.textContent = api.formatCurrency(cartData.totalAmount);
            }
        } catch (e) {
            console.error('Failed to load checkout summary', e);
        }
    },

    async confirmOrder(event) {
        if (event) event.preventDefault();

        const buyerName = document.getElementById('checkout-name').value.trim();
        const email = document.getElementById('checkout-email').value.trim();
        const phone = document.getElementById('checkout-phone').value.trim();
        const deliveryAddress = document.getElementById('checkout-address').value.trim();

        if (!buyerName || !email || !phone || !deliveryAddress) {
            api.showAlert('Please fill in all delivery and contact details.', 'error');
            return;
        }

        const confirmBtn = document.getElementById('confirm-order-btn');
        if (confirmBtn) {
            confirmBtn.disabled = true;
            confirmBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Processing Order...';
        }

        try {
            const result = await api.post('/orders', {
                buyerName,
                email,
                phone,
                deliveryAddress
            });

            // Show Success Modal
            const modal = document.getElementById('order-success-modal');
            const orderIdSpan = document.getElementById('success-order-id');
            const totalSpan = document.getElementById('success-order-total');

            if (orderIdSpan) orderIdSpan.textContent = result.orderNumber || 'RTH10001';
            if (totalSpan) totalSpan.textContent = api.formatCurrency(result.totalAmount);

            if (modal) {
                modal.classList.add('active');
            } else {
                alert(`ORDER SUCCESSFUL!\nYour Order ID is: ${result.orderNumber}\nTotal Amount: ${api.formatCurrency(result.totalAmount)}`);
                window.location.href = '/orders.html';
            }

            auth.refreshCartBadge();
        } catch (e) {
            api.showAlert(e.message || 'Failed to place order. Insufficient stock available.', 'error');
            if (confirmBtn) {
                confirmBtn.disabled = false;
                confirmBtn.innerHTML = '<i class="fa-solid fa-lock"></i> Confirm Order';
            }
        }
    },

    async initOrdersList() {
        if (!auth.requireAuth(['BUYER', 'SELLER', 'ADMIN'])) return;

        const tableBody = document.getElementById('orders-table-body');
        const emptyState = document.getElementById('orders-empty-state');

        try {
            const orders = await api.get('/orders');
            if (!orders || orders.length === 0) {
                if (emptyState) emptyState.style.display = 'block';
                return;
            }

            if (emptyState) emptyState.style.display = 'none';

            if (tableBody) {
                tableBody.innerHTML = orders.map(order => {
                    const statusClass = `badge-${(order.status || 'pending').toLowerCase()}`;
                    const itemsSummary = (order.items || []).map(i => `${i.productName} (×${i.quantity})`).join(', ');

                    return `
                        <tr>
                            <td><strong style="color: var(--primary);">${order.orderNumber}</strong></td>
                            <td>${itemsSummary || 'Standard Package'}</td>
                            <td><strong style="color: var(--secondary);">${api.formatCurrency(order.totalAmount)}</strong></td>
                            <td>${api.formatDate(order.createdAt)}</td>
                            <td><span class="badge ${statusClass}">${order.status}</span></td>
                            <td>
                                ${order.status === 'Pending' ? `
                                    <button class="btn btn-outline btn-sm" style="color: var(--error); border-color: #f0c5c5;"
                                            onclick="ordersManager.cancelOrder(${order.id})">
                                        Cancel
                                    </button>
                                ` : `
                                    <span style="font-size: 0.85rem; color: var(--text-secondary);">Completed</span>
                                `}
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        } catch (e) {
            console.error('Failed to load orders', e);
        }
    },

    async cancelOrder(orderId) {
        if (!confirm('Are you sure you want to cancel this order? The product stock will be safely restored.')) {
            return;
        }

        try {
            await api.put(`/orders/${orderId}/status`, { status: 'Cancelled' });
            api.showAlert('Order cancelled successfully.', 'success');
            await this.initOrdersList();
        } catch (e) {
            api.showAlert(e.message || 'Failed to cancel order', 'error');
        }
    }
};
