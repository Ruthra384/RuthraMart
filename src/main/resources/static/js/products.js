/**
 * RUTHRAMART - PRODUCT CATALOG & SEARCH
 */

const productsManager = {
    products: [],
    categories: [],
    selectedCategory: 'All',
    searchQuery: '',

    async init() {
        const urlParams = new URLSearchParams(window.location.search);
        this.selectedCategory = urlParams.get('category') || 'All';
        this.searchQuery = urlParams.get('query') || '';

        const searchInput = document.getElementById('search-input');
        if (searchInput && this.searchQuery) {
            searchInput.value = this.searchQuery;
        }

        await this.loadCategories();
        await this.loadProducts();
    },

    async loadCategories() {
        try {
            this.categories = await api.get('/categories');
            this.renderCategoryFilters();
        } catch (e) {
            console.error('Failed to load categories', e);
        }
    },

    renderCategoryFilters() {
        const filterContainer = document.getElementById('category-filter-list');
        if (!filterContainer) return;

        let html = `
            <button class="category-filter-btn ${this.selectedCategory === 'All' ? 'active' : ''}" 
                    onclick="productsManager.selectCategory('All')">
                All
            </button>
        `;

        this.categories.forEach(cat => {
            const isActive = this.selectedCategory.toLowerCase() === cat.name.toLowerCase();
            html += `
                <button class="category-filter-btn ${isActive ? 'active' : ''}" 
                        onclick="productsManager.selectCategory('${cat.name}')">
                    ${cat.name}
                </button>
            `;
        });

        filterContainer.innerHTML = html;
    },

    selectCategory(catName) {
        this.selectedCategory = catName;
        this.renderCategoryFilters();
        this.loadProducts();
    },

    handleSearch(query) {
        this.searchQuery = query;
        this.loadProducts();
    },

    async loadProducts() {
        const grid = document.getElementById('products-grid');
        if (!grid) return;

        grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; padding: 40px;"><i class="fa-solid fa-spinner fa-spin fa-2x" style="color: var(--primary);"></i><p style="margin-top: 10px;">Loading thoughtful finds...</p></div>';

        try {
            let endpoint = '/products';
            const params = [];
            if (this.selectedCategory && this.selectedCategory !== 'All') {
                params.push(`category=${encodeURIComponent(this.selectedCategory)}`);
            }
            if (this.searchQuery && this.searchQuery.trim()) {
                params.push(`query=${encodeURIComponent(this.searchQuery.trim())}`);
            }
            if (params.length > 0) {
                endpoint += `?${params.join('&')}`;
            }

            this.products = await api.get(endpoint);
            this.renderProducts(this.products);
        } catch (e) {
            grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: var(--error);">Failed to load products. Please check server.</div>`;
        }
    },

    renderProducts(items) {
        const grid = document.getElementById('products-grid');
        if (!grid) return;

        if (!items || items.length === 0) {
            grid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 60px 20px;">
                    <i class="fa-solid fa-box-open fa-3x" style="color: var(--light); margin-bottom: 16px;"></i>
                    <h3 style="color: var(--secondary);">No Products Found</h3>
                    <p style="color: var(--text-secondary); margin-top: 8px;">Try searching for another keyword or browse other categories.</p>
                </div>
            `;
            return;
        }

        grid.innerHTML = items.map(p => `
            <div class="product-card">
                <div class="product-image-wrap">
                    <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80'}" alt="${p.name}" loading="lazy">
                    <span class="product-badge">${p.category}</span>
                </div>
                <div class="product-info">
                    <div class="product-category">${p.category}</div>
                    <a href="/product-details.html?id=${p.id}" class="product-title">${p.name}</a>
                    <div class="product-rating">
                        <i class="fa-solid fa-star"></i>
                        <i class="fa-solid fa-star"></i>
                        <i class="fa-solid fa-star"></i>
                        <i class="fa-solid fa-star"></i>
                        <i class="fa-solid fa-star-half-stroke"></i>
                        <span>(4.8)</span>
                    </div>
                    <div class="product-price-row">
                        <div class="product-price">${api.formatCurrency(p.price)}</div>
                        <div class="product-stock ${p.stockQuantity <= 0 ? 'out-of-stock' : ''}">
                            ${p.stockQuantity > 0 ? `In Stock (${p.stockQuantity})` : 'Out of Stock'}
                        </div>
                    </div>
                    <div class="product-actions">
                        <button class="btn btn-primary btn-block btn-sm" 
                                onclick="productsManager.quickAddToCart(${p.id}, ${p.stockQuantity})"
                                ${p.stockQuantity <= 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-cart-shopping"></i> Add to Cart
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    },

    async quickAddToCart(productId, availableStock) {
        if (!auth.isLoggedIn()) {
            if (confirm('Please log in as a Buyer to add items to your cart. Go to login page now?')) {
                window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);
            }
            return;
        }

        if (availableStock <= 0) {
            alert('Insufficient stock available.');
            return;
        }

        try {
            await api.post('/cart', { productId, quantity: 1 });
            api.showAlert('Added to cart successfully!', 'success');
            auth.refreshCartBadge();
        } catch (e) {
            api.showAlert(e.message || 'Failed to add item to cart', 'error');
        }
    }
};
