# RUTHRAMART — Comprehensive Project Documentation
**Multi-Seller E-Commerce Full-Stack Capstone Project**

---

## 1. Project Overview
**RuthraMart** is a comprehensive, production-ready, full-stack multi-seller online shopping platform designed for a college capstone project. Inspired by real-world e-commerce leaders like Amazon and Flipkart, RuthraMart offers specialized portals for **Buyers**, **Sellers**, and **Administrators** with role-based access control (RBAC), end-to-end relational persistence, inventory deduction transactions, and an inventory-aware **AI Chatbot Assistant**.

---

## 2. Technology Stack & Architecture

### Backend Architecture
- **Language**: Java 21 LTS (with C++20 Drogon Framework architectural specification)
- **Framework**: Spring Boot 3 / 4 MVC REST Services
- **Data Persistence**: Spring Data JPA & Hibernate
- **Database**:
  - **Embedded Persistent File Storage**: H2 Database (`./data/ruthramart`) with MySQL compatibility mode for instant zero-config startup on any computer without installing servers.
  - **Relational Cloud / Local Production**: MySQL 8.x / PostgreSQL compatible via simple environment variable `SPRING_DATASOURCE_URL`.
- **Security**: Salted SHA-256 cryptographic password hashing, token session tracking, role-based access control, server-side price recalculation, and stock concurrency management.

### Frontend Architecture
- **Markup & Layout**: Semantic HTML5 with responsive flexbox and CSS Grid
- **Styling**: Pure CSS3 structured around the exact Sage Green, Deep Olive, Beige, and Warm Ivory aesthetic palette
- **Iconography & Fonts**: Font Awesome 6 Pro Free CDN + Google Fonts (Plus Jakarta Sans)
- **Client Scripting**: Vanilla JavaScript with modular API clients (`api.js`, `auth.js`, `products.js`, `cart.js`, `orders.js`, `reviews.js`, `chatbot.js`)

---

## 3. Brand Identity & Aesthetic Color Theme

RuthraMart adheres to a cohesive, calm, and premium earth-tone visual palette:

| Element | Color Name | HEX Code | RGB | Usage |
|---|---|---|---|---|
| **Primary** | Forest Sage | `#526B55` | `82, 107, 85` | Primary buttons, active highlights, key links |
| **Secondary** | Deep Olive | `#354638` | `53, 70, 56` | Navbar, footer, headings, admin sidebar |
| **Light Accent** | Soft Sage | `#A8B9A0` | `168, 185, 160` | Input focus rings, badges, hover accents |
| **Card Color** | Warm Beige | `#E9E0D2` | `233, 224, 210` | Summary cards, category tiles, review forms |
| **Background** | Warm Ivory | `#FAF8F2` | `250, 248, 242` | Main page background, section surrounding |
| **Surface** | Pure White | `#FFFFFF` | `255, 255, 255` | Product cards, form cards, clean spacing |
| **Success** | Olive Green | `#3F7650` | `63, 118, 80` | Stock indicators, order confirmed badges |
| **Danger** | Rust Crimson | `#B84A4A` | `184, 74, 74` | Cancel buttons, out of stock, errors |

---

## 4. User Roles & Access Control

### 1. Buyer
- Browse catalog, search by keywords, filter across 6 departments.
- Add products to shopping cart with real-time stock limits.
- Complete checkout with contact details and delivery addresses.
- View previous orders with live status: `Pending` → `Confirmed` → `Shipped` → `Delivered`.
- Safely cancel pending orders with automatic inventory restoration.
- Leave 1-to-5 star ratings and honest customer reviews.

### 2. Seller
- Dedicated Seller Hub dashboard displaying total listings, orders, and sales revenue.
- Add new products with categories, prices, stock, descriptions, and images.
- Edit pricing, description, and inventory for self-owned listings.
- Delete self-owned listings.
- Review orders received from customers and update fulfillment status.

### 3. Administrator
- Master Admin Console with 6 Key Performance Indicators (Total Users, Buyers, Sellers, Products, Orders, Revenue).
- User management: inspect all registered buyers and sellers.
- Product moderation: view all vendor listings and safely remove inappropriate items with a two-step confirmation modal.
- Global order control: inspect all platform transactions and adjust fulfillment states.

---

## 5. Relational Database Design

```
+------------------+       +------------------+       +------------------+
|      users       |       |    categories    |       |     products     |
+------------------+       +------------------+       +------------------+
| id (PK)          |<---+  | id (PK)          |  +--->| id (PK)          |
| name             |    |  | name (UNIQUE)    |  |    | name             |
| email (UNIQUE)   |    |  | description      |  |    | category         |
| phone            |    |  | icon             |  |    | price            |
| password (HASH)  |    |  +------------------+  |    | stock_quantity   |
| role             |    |                        |    | image_url        |
| created_at       |    |                        |    | seller_id (FK)---+
+------------------+    |                        |    | seller_name      |
   |         |          |                        |    | created_at       |
   |         |          |                        |    +------------------+
   |         |          |                        |        |        |
   |         v          |                        |        |        |
   |   +------------+   |                        |        |        |
   |   | cart_items |   |                        |        |        |
   |   +------------+   |                        |        |        |
   |   | id (PK)    |   |                        |        |        |
   |   | user_id(FK)+---+                        |        |        |
   |   | product(FK)+----------------------------+        |        |
   |   | quantity   |                                     |        |
   |   +------------+                                     |        |
   |                                                      |        |
   v                                                      v        |
+------------------+    +------------------+       +------------+  |
|      orders      |--->|   order_items    |<------+  reviews   |<-+
+------------------+    +------------------+       +------------+
| id (PK)          |    | id (PK)          |       | id (PK)    |
| order_number(UNQ)|    | order_id (FK)    |       | product_id |
| buyer_id (FK)    |    | product_id (FK)  |       | buyer_id   |
| buyer_name       |    | seller_id        |       | rating(1-5)|
| email, phone     |    | price, quantity  |       | comment    |
| delivery_address |    | subtotal         |       | review_date|
| total_amount     |    +------------------+       +------------+
| status           |
| created_at       |
+------------------+
```

---

## 6. Pre-Configured Demonstration Accounts

| Role | Email | Password | Pre-seeded Activity |
|---|---|---|---|
| **Admin** | `admin@ruthramart.com` | `Admin@123` | System oversight & full access |
| **Seller 1** | `seller1@ruthramart.com` | `Seller@123` | TechZone Electronics (Laptops, Mobiles, Headphones) |
| **Seller 2** | `seller2@ruthramart.com` | `Seller@123` | Elite Trendz & Living (Kurti, Shirts, Vases, Handbags) |
| **Buyer 1** | `buyer1@ruthramart.com` | `Buyer@123` | Arun Kumar (Order #RTH10001 Delivered, 2 Reviews) |
| **Buyer 2** | `buyer2@ruthramart.com` | `Buyer@123` | Priya Sharma (Order #RTH10002 Shipped, 2 Reviews) |
| **Buyer 3** | `buyer3@ruthramart.com` | `Buyer@123` | Kavitha R (Clean Code Book Review) |

---

## 7. REST API Endpoints Reference

### Authentication
- `POST /api/register`: Register new Buyer or Seller.
- `POST /api/login`: Authenticate and obtain session token.
- `POST /api/logout`: Terminate active session token.
- `GET /api/me`: Get current authenticated user details.

### Catalog & Products
- `GET /api/products`: Retrieve catalog (supports `?category=...&query=...`).
- `GET /api/products/{id}`: Detailed product information.
- `POST /api/products`: Create new product (Seller / Admin).
- `PUT /api/products/{id}`: Edit product details & stock (Seller owner / Admin).
- `DELETE /api/products/{id}`: Delete product (Seller owner / Admin).
- `GET /api/products/seller`: Retrieve listings for current seller.

### Categories
- `GET /api/categories`: Get all 6 categories with descriptions and icons.

### Shopping Cart
- `GET /api/cart`: Retrieve buyer's current cart items, subtotal, and total.
- `POST /api/cart`: Add product to cart with inventory availability check.
- `PUT /api/cart/{id}`: Update item quantity in cart.
- `DELETE /api/cart/{id}`: Remove item from cart.
- `DELETE /api/cart/clear`: Clear all items from cart.

### Orders & Checkout
- `POST /api/orders`: Transactional checkout with stock reduction and order ID generation (`RTH#####`).
- `GET /api/orders`: Get orders for buyer, seller, or admin.
- `GET /api/orders/{id}`: Get detailed order breakdown.
- `PUT /api/orders/{id}/status`: Update order fulfillment status or cancel pending orders.

### Reviews & Ratings
- `GET /api/products/{id}/reviews`: Retrieve reviews, average star rating, and review count.
- `POST /api/reviews`: Submit 1-to-5 star rating and comment for a product.

### Administrative Control
- `GET /api/admin/stats`: Get platform metrics (Total Users, Buyers, Sellers, Products, Orders, Sales).
- `GET /api/admin/users`: List all users with phone and role.
- `GET /api/admin/products`: Full product inspection with merchant names.
- `DELETE /api/admin/products/{id}`: Remove inappropriate listing.
- `GET /api/admin/orders`: Global order audit log.

### AI Shopping Assistant
- `POST /api/chatbot`: Process shopping query and return inventory-aware recommendation.
