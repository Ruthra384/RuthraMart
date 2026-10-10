/**
 * RUTHRAMART - SHOPPING CART MANAGER
 */

const cartManager = {
    cartData: { items: [], totalAmount: 0, totalItems: 0 },

    async init() {
        if (!auth.requireAuth(['BUYER', 'ADMIN'])) return;
        await this.loadCart();
    },

    async loadCart() {
        const container = document.getElementById('cart-items-container');
        const emptyState = document.getElementById('cart-empty-state');
        const summaryCard = document.getElementById('cart-summary-section');

        try {
            this.cartData = await api.get('/cart');
            auth.refreshCartBadge();

            if (!this.cartData.items || this.cartData.items.length === 0) {
                if (container) container.style.display = 'none';
                if (summaryCard) summaryCard.style.display = 'none';
                if (emptyState) emptyState.style.display = 'block';
                return;
            }

            if (emptyState) emptyState.style.display = 'none';
            if (container) container.style.display = 'block';
            if (summaryCard) summaryCard.style.display = 'block';

            this.renderCartItems();
            this.renderSummary();
        } catch (e) {
            console.error('Failed to load cart', e);
            api.showAlert('Failed to load shopping cart', 'error');
        }
    },

    renderCartItems() {
        const list = document.getElementById('cart-items-list');
        if (!list) return;

        list.innerHTML = this.cartData.items.map(item => `
            <div class="cart-item-card" id="cart-item-${item.id}">
                <img src="${item.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80'}" 
                     alt="${item.name}" class="cart-item-img">
                <div>
                    <h4 style="font-size: 1.05rem; color: var(--secondary); margin-bottom: 4px;">
                        <a href="/product-details.html?id=${item.productId}">${item.name}</a>
                    </h4>
                    <span style="font-size: 0.85rem; color: var(--text-secondary); text-transform: uppercase;">
                        ${item.category}
                    </span>
                    <div style="font-size: 0.95rem; font-weight: 700; color: var(--primary); margin-top: 4px;">
                        Unit Price: ${api.formatCurrency(item.price)}
                    </div>
                </div>
                <div class="cart-qty-control">
                    <button class="cart-qty-btn" onclick="cartManager.updateQuantity(${item.id}, ${item.quantity - 1})">-</button>
                    <span class="cart-qty-val">${item.quantity}</span>
                    <button class="cart-qty-btn" onclick="cartManager.updateQuantity(${item.id}, ${item.quantity + 1})">+</button>
                </div>
                <div style="font-weight: 800; font-size: 1.15rem; color: var(--secondary);">
                    ${api.formatCurrency(item.subtotal)}
                </div>
                <div>
                    <button class="btn btn-outline btn-sm" style="color: var(--error); border-color: #e5bebe;" 
                            onclick="cartManager.removeItem(${item.id})" title="Remove item">
                        <i class="fa-solid fa-trash-can"></i>
                    </button>
                </div>
            </div>
        `).join('');
    },

    renderSummary() {
        const subtotalEl = document.getElementById('cart-subtotal');
        const totalEl = document.getElementById('cart-total');
        const itemCountEl = document.getElementById('cart-item-count');

        if (subtotalEl) subtotalEl.textContent = api.formatCurrency(this.cartData.totalAmount);
        if (totalEl) totalEl.textContent = api.formatCurrency(this.cartData.totalAmount);
        if (itemCountEl) itemCountEl.textContent = this.cartData.totalItems;
    },

    async updateQuantity(cartItemId, newQuantity) {
        if (newQuantity <= 0) {
            if (confirm('Do you want to remove this item from your cart?')) {
                await this.removeItem(cartItemId);
            }
            return;
        }

        try {
            await api.put(`/cart/${cartItemId}`, { quantity: newQuantity });
            await this.loadCart();
        } catch (e) {
            api.showAlert(e.message || 'Insufficient stock available.', 'error');
        }
    },

    async removeItem(cartItemId) {
        try {
            await api.delete(`/cart/${cartItemId}`);
            api.showAlert('Item removed from cart.', 'success');
            await this.loadCart();
        } catch (e) {
            api.showAlert('Failed to remove item', 'error');
        }
    },

    async clearCart() {
        if (!confirm('Are you sure you want to clear your entire cart?')) return;
        try {
            await api.delete('/cart/clear');
            api.showAlert('Cart cleared.', 'success');
            await this.loadCart();
        } catch (e) {
            api.showAlert('Failed to clear cart', 'error');
        }
    }
};
