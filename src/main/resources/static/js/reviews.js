/**
 * RUTHRAMART - PRODUCT REVIEWS & RATINGS
 */

const reviewsManager = {
    currentProductId: null,
    selectedRating: 5,

    async init(productId) {
        this.currentProductId = productId;
        this.setupStarRatingSelector();
        await this.loadReviews();
    },

    setupStarRatingSelector() {
        const starContainer = document.getElementById('star-rating-selector');
        if (!starContainer) return;

        starContainer.innerHTML = [1, 2, 3, 4, 5].map(num => `
            <i class="fa-solid fa-star star-rate-btn" data-value="${num}" 
               style="font-size: 1.5rem; cursor: pointer; color: ${num <= this.selectedRating ? '#e59819' : '#d8d8cc'}; margin-right: 4px;"
               onclick="reviewsManager.setRating(${num})"></i>
        `).join('');
    },

    setRating(rating) {
        this.selectedRating = rating;
        const stars = document.querySelectorAll('.star-rate-btn');
        stars.forEach(s => {
            const val = parseInt(s.getAttribute('data-value'));
            s.style.color = val <= rating ? '#e59819' : '#d8d8cc';
        });
    },

    async loadReviews() {
        if (!this.currentProductId) return;

        const reviewsList = document.getElementById('reviews-list');
        const avgRatingEl = document.getElementById('product-avg-rating');
        const totalReviewsEl = document.getElementById('product-total-reviews');

        try {
            const data = await api.get(`/products/${this.currentProductId}/reviews`);
            const avg = data.averageRating || 0;
            const total = data.totalReviews || 0;

            if (avgRatingEl) avgRatingEl.textContent = avg.toFixed(1);
            if (totalReviewsEl) totalReviewsEl.textContent = `(${total} ${total === 1 ? 'review' : 'reviews'})`;

            if (reviewsList) {
                if (!data.reviews || data.reviews.length === 0) {
                    reviewsList.innerHTML = `
                        <div style="text-align: center; padding: 24px; color: var(--text-secondary);">
                            <p>No reviews yet. Be the first to review this product!</p>
                        </div>
                    `;
                    return;
                }

                reviewsList.innerHTML = data.reviews.map(r => {
                    let starsHtml = '';
                    for (let i = 1; i <= 5; i++) {
                        starsHtml += `<i class="fa-solid fa-star" style="color: ${i <= r.rating ? '#e59819' : '#d8d8cc'}; font-size: 0.85rem;"></i>`;
                    }

                    return `
                        <div style="padding: 18px 0; border-bottom: 1px solid #f0eee8;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                                <div style="display: flex; align-items: center; gap: 8px;">
                                    <strong style="color: var(--secondary);">${r.buyerName}</strong>
                                    <span style="font-size: 0.75rem; background: var(--secondary-section); color: var(--primary); padding: 2px 8px; border-radius: var(--radius-full); font-weight: 600;">Verified Buyer</span>
                                </div>
                                <span style="font-size: 0.8rem; color: var(--text-secondary);">${api.formatDate(r.reviewDate)}</span>
                            </div>
                            <div style="margin-bottom: 8px;">${starsHtml}</div>
                            <p style="color: var(--text-dark); font-size: 0.95rem; line-height: 1.5;">${r.comment}</p>
                        </div>
                    `;
                }).join('');
            }
        } catch (e) {
            console.error('Failed to load reviews', e);
        }
    },

    async submitReview(event) {
        if (event) event.preventDefault();

        if (!auth.isLoggedIn()) {
            alert('Please log in as a Buyer to leave a product review.');
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);
            return;
        }

        const commentInput = document.getElementById('review-comment');
        const comment = commentInput ? commentInput.value.trim() : '';

        if (!comment) {
            alert('Please write your review thoughts before submitting.');
            return;
        }

        try {
            await api.post('/reviews', {
                productId: this.currentProductId,
                rating: this.selectedRating,
                comment
            });

            if (commentInput) commentInput.value = '';
            api.showAlert('Thank you! Your review has been submitted.', 'success');
            await this.loadReviews();
        } catch (e) {
            api.showAlert(e.message || 'Failed to submit review', 'error');
        }
    }
};
