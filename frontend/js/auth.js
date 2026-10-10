/**
 * RUTHRAMART - AUTHENTICATION & ROLE MANAGEMENT
 */

const auth = {
    getUser() {
        const u = localStorage.getItem('ruthramart_user');
        return u ? JSON.parse(u) : null;
    },

    getToken() {
        return localStorage.getItem('ruthramart_token');
    },

    isLoggedIn() {
        return !!this.getToken();
    },

    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },

    async register(name, email, phone, password, confirmPassword, role) {
        return api.post('/register', {
            name,
            email,
            phone,
            password,
            confirmPassword,
            role
        });
    },

    async login(email, password) {
        const data = await api.post('/login', { email, password });
        if (data.token && data.user) {
            localStorage.setItem('ruthramart_token', data.token);
            localStorage.setItem('ruthramart_user', JSON.stringify(data.user));
            this.redirectByRole(data.user.role);
        }
        return data;
    },

    async logout() {
        try {
            await api.post('/logout', {});
        } catch (e) {
            // Ignore failure on logout call
        }
        localStorage.removeItem('ruthramart_token');
        localStorage.removeItem('ruthramart_user');
        window.location.href = '/login.html';
    },

    redirectByRole(role) {
        if (!role) {
            window.location.href = '/index.html';
            return;
        }
        const upper = role.toUpperCase();
        if (upper === 'ADMIN') {
            window.location.href = '/admin-dashboard.html';
        } else if (upper === 'SELLER') {
            window.location.href = '/seller-dashboard.html';
        } else {
            window.location.href = '/products.html';
        }
    },

    requireAuth(allowedRoles = []) {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
            return false;
        }
        if (allowedRoles.length > 0) {
            const role = this.getRole();
            if (!role || !allowedRoles.includes(role.toUpperCase())) {
                alert('Access Denied: You do not have permission to view this page.');
                window.location.href = '/index.html';
                return false;
            }
        }
        return true;
    },

    updateNavbar() {
        const navAuthContainer = document.getElementById('nav-auth-links');
        const user = this.getUser();

        if (navAuthContainer) {
            if (user) {
                let roleDashboard = '';
                if (user.role === 'ADMIN') {
                    roleDashboard = `<a href="/admin-dashboard.html" class="nav-link"><i class="fa-solid fa-gauge"></i> Admin</a>`;
                } else if (user.role === 'SELLER') {
                    roleDashboard = `<a href="/seller-dashboard.html" class="nav-link"><i class="fa-solid fa-store"></i> Seller Hub</a>`;
                }

                navAuthContainer.innerHTML = `
                    ${roleDashboard}
                    <a href="/profile.html" class="nav-link"><span class="user-badge">${user.name.split(' ')[0]}</span></a>
                    <a href="javascript:void(0)" onclick="auth.logout()" class="nav-link"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
                `;
            } else {
                navAuthContainer.innerHTML = `
                    <a href="/login.html" class="nav-link"><i class="fa-solid fa-user"></i> Login</a>
                    <a href="/register.html" class="btn btn-primary btn-sm">Register</a>
                `;
            }
        }

        // Mobile Nav Toggle setup
        const navToggle = document.getElementById('nav-toggle');
        const navLinks = document.getElementById('nav-links');
        if (navToggle && navLinks) {
            navToggle.addEventListener('click', () => {
                navLinks.classList.toggle('show');
            });
        }

        // Update Cart count if on a page with cart badge
        this.refreshCartBadge();
    },

    async refreshCartBadge() {
        const cartBadge = document.getElementById('cart-count');
        if (!cartBadge) return;

        if (!this.isLoggedIn() || this.getRole() !== 'BUYER') {
            cartBadge.textContent = '0';
            return;
        }

        try {
            const cartData = await api.get('/cart');
            cartBadge.textContent = cartData.totalItems || 0;
        } catch (e) {
            cartBadge.textContent = '0';
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    auth.updateNavbar();
});
