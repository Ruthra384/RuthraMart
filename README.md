# RUTHRAMART — Multi Seller Online Shopping Platform
> **Complete Full-Stack E-Commerce College Capstone Project**  
> **Brand Palette:** Forest Sage (`#526B55`), Deep Olive (`#354638`), Soft Sage (`#A8B9A0`), Warm Beige (`#E9E0D2`), and Warm Ivory (`#FAF8F2`)

---

## 🌟 Executive Summary
**RuthraMart** is a complete, functioning, individual full-stack multi-seller e-commerce web application engineered for final college capstone evaluations. It delivers an intuitive shopping experience inspired by platforms like Amazon and Flipkart, with dedicated role-based portals for **Buyers**, **Sellers**, and **Administrators**, real-time inventory decrementing transactions, and an inventory-aware **AI Chatbot Assistant**.

---

## 🛠️ Technology Stack
- **Primary Full-Stack Engine:** Java 21 LTS + Spring Boot 3 / 4 MVC REST APIs
- **Build System:** Apache Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **Database Engine:** Embedded Persistent H2 File Database (`./data/ruthramart`) with MySQL Mode (Zero setup required; runs instantly out of the box). Also supports MySQL 8.x and PostgreSQL.
- **Frontend Stack:** Semantic HTML5, Custom Responsive CSS3, Modular ES6 JavaScript
- **C++20 Architecture Specification:** Drogon C++ Framework entrypoint and `CMakeLists.txt` included in `backend/` and repository root.
- **External AI Integration:** Pluggable Google Gemini (`GEMINI_API_KEY`) / OpenAI API with an intelligent rule-based, database-aware fallback engine that operates 100% offline.

---

## 🎨 Color Palette & Design System

The application strictly uses the requested calming earthy aesthetic:

- **Primary Color (Forest Sage):** `#526B55` — Main action buttons, active navigation states, primary calls-to-action.
- **Secondary Color (Deep Olive):** `#354638` — Navigation bar, footer, primary headings, admin sidebars.
- **Light Color (Soft Sage):** `#A8B9A0` — Focus effects, badges, supporting UI elements, hover states.
- **Card Color (Warm Beige):** `#E9E0D2` — Category cards, order summary containers, soft backgrounds.
- **Background Color (Warm Ivory):** `#FAF8F2` — Main page body background, card surroundings.
- **Clean White:** `#FFFFFF` — Product cards, modal forms, inputs, surface layers.

---

## 👥 Default Accounts for Demonstration & Evaluation

RuthraMart automatically initializes the database on first run with complete demonstration data:

| Role | Account Email | Password | Details & Pre-seeded State |
|---|---|---|---|
| **ADMIN** | `admin@ruthramart.com` | `Admin@123` | Master control over users, products, and orders |
| **SELLER 1** | `seller1@ruthramart.com` | `Seller@123` | TechZone Electronics (Laptops, Mobiles, Headphones) |
| **SELLER 2** | `seller2@ruthramart.com` | `Seller@123` | Elite Trendz & Living (Kurti, Shirts, Vases, Handbags) |
| **BUYER 1** | `buyer1@ruthramart.com` | `Buyer@123` | Arun Kumar (Order #RTH10001 Delivered, 2 reviews) |
| **BUYER 2** | `buyer2@ruthramart.com` | `Buyer@123` | Priya Sharma (Order #RTH10002 Shipped, 2 reviews) |
| **BUYER 3** | `buyer3@ruthramart.com` | `Buyer@123` | Kavitha R (Clean Code Book review) |

*Note: You can also register new Buyer and Seller accounts directly through the registration page.*

---

## 🚀 How to Run RuthraMart (Zero Setup Required)

### Step 1: Open Terminal in Project Directory
```powershell
cd c:\Users\ELCOT\Downloads\RuthraMart
```

### Step 2: Start the Full-Stack Application
Using Maven Wrapper on Windows:
```powershell
.\mvnw.cmd spring-boot:run
```
*(On macOS/Linux, use `./mvnw spring-boot:run`)*

### Step 3: Open in Your Browser
Navigate to:
```
http://localhost:8080
```
That's it! The database, sample products, categories, reviews, orders, and demo accounts are all automatically loaded and ready for live demonstration.

---

## 📦 Core Feature Modules

### 1. Buyer Module
- **Live Search & Category Filtering:** Search by product name, category, or description with backend query processing across Electronics, Fashion, Accessories, Home, Beauty, and Books.
- **Product Details & Customer Reviews:** High-resolution product images, live stock indicators, seller identity, verified star ratings (★★★★★), and honest written reviews.
- **Shopping Cart:** Add items, adjust quantities, view unit price and subtotal, and enforce real-time stock limits (*"Insufficient stock available"*).
- **Checkout & Order Confirmation:** Recipient address entry, cash on delivery simulation, and atomic order creation with unique Order IDs (`RTH10001`, `RTH10002`...).
- **Order Tracking & Cancellation:** Review order timeline (`Pending` → `Confirmed` → `Shipped` → `Delivered`) and cancel pending orders with automatic stock replenishment.

### 2. Seller Module
- **Merchant Center Dashboard:** Real-time metrics for Total Products, Total Orders, and Revenue.
- **Catalog Management (CRUD):** Add new listings, edit pricing and stock, and delete self-owned items.
- **Order Fulfillment:** Inspect received customer orders and advance shipping status.

### 3. Administrator Module
- **Executive Analytics:** Overview cards for Total Users, Total Buyers, Total Sellers, Total Products, Total Orders, and Total Sales.
- **User Oversight:** Inspect registered buyers and sellers with role filtering.
- **Product Moderation:** Review platform listings and remove inappropriate products with a confirmation modal.
- **Global Order Audit:** System-wide order visibility and status management.

### 4. RuthraMart AI Shopping Assistant
- Floating widget on all pages: *"Hi! I'm RuthraMart AI Assistant. How can I help you?"*
- Understands natural language requests (e.g., *"I need a laptop"*, *"How to add to cart?"*, *"Where are my orders?"*, *"What categories do you have?"*).
- Directly queries live product database inventory to recommend in-stock items with prices.
- Optional external AI API support via `GEMINI_API_KEY` with seamless rule-based fallback.

---

## 📂 Project Directory Structure

```
RuthraMart/
├── CMakeLists.txt                    # C++20 Drogon build definition
├── pom.xml                           # Java Maven configuration & dependencies
├── README.md                         # Project overview and instructions
├── .env.example                      # Configuration and environment template
│
├── backend/                          # C++ Drogon Framework implementation
│   ├── main.cpp
│   └── config/config.json
│
├── database/
│   ├── schema.sql                    # Relational schema (PKs, FKs, constraints, indexes)
│   └── seed.sql                      # Demonstration data (Users, Products, Orders, Reviews)
│
├── frontend/                         # Clean static web application
│   ├── index.html                    # Homepage (Hero, Categories, Featured Products)
│   ├── login.html                    # Role-based login with demo credentials
│   ├── register.html                 # Buyer & Seller registration with validation
│   ├── products.html                 # Catalog with real search & category filter bar
│   ├── product-details.html          # Product specs, reviews list, and review form
│   ├── cart.html                     # Shopping cart with quantity & total calculations
│   ├── checkout.html                 # Address details & order confirmation
│   ├── orders.html                   # Buyer order history and status tracking
│   ├── profile.html                  # User account profile
│   ├── 404.html                      # Not Found error page
│   ├── seller-dashboard.html         # Seller Hub metrics
│   ├── seller-products.html          # Seller product CRUD
│   ├── seller-orders.html            # Seller order fulfillment
│   ├── admin-dashboard.html          # Admin 6-KPI dashboard
│   ├── admin-users.html              # Admin user management
│   ├── admin-products.html           # Admin product moderation
│   ├── admin-orders.html             # Admin platform orders
│   ├── seller/                       # Subfolder route compatibility
│   ├── admin/                        # Subfolder route compatibility
│   ├── css/
│   │   └── style.css                 # Master Sage Green & Deep Olive stylesheet
│   └── js/
│       ├── api.js                    # HTTP client, token management, formatting
│       ├── auth.js                   # Authentication & role redirection
│       ├── products.js               # Catalog fetching, search, filter
│       ├── cart.js                   # Cart state & stock limit enforcement
│       ├── orders.js                 # Checkout, order placement, cancellation
│       ├── reviews.js                # Star rating calculation & submission
│       └── chatbot.js                # AI Chatbot interactive widget
│
├── src/main/java/com/ruthramart/RuthraMart/
│   ├── RuthraMartApplication.java    # Spring Boot entry point
│   ├── config/
│   │   ├── DataInitializer.java      # Auto-seeds Admin, Sellers, Buyers, Products
│   │   └── WebConfig.java            # CORS & Static resource mappings
│   ├── controller/
│   │   ├── AuthController.java       # Register, login, logout, me
│   │   ├── CategoryController.java   # Category catalog
│   │   ├── ProductController.java    # Product CRUD & search
│   │   ├── CartController.java       # Cart management & stock validation
│   │   ├── OrderController.java      # Transactional order placement & status
│   │   ├── ReviewController.java     # Product ratings & reviews
│   │   ├── AdminController.java      # Admin statistics & moderation
│   │   └── ChatbotController.java    # AI shopping assistant
│   ├── model/
│   │   ├── User.java
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Review.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CartItemRepository.java
│   │   ├── OrderRepository.java
│   │   ├── OrderItemRepository.java
│   │   └── ReviewRepository.java
│   ├── service/
│   │   ├── AuthService.java          # Session management & credential checks
│   │   └── ChatbotService.java       # AI NLP & rule-based shopping engine
│   └── util/
│       └── PasswordUtil.java         # SHA-256 salted password cryptography
│
└── docs/
    └── project-documentation.md      # Detailed architectural and viva documentation
```

---

## 🔒 Security & Data Integrity Highlights
1. **Password Encryption:** Passwords are never stored in plain text. Salted SHA-256 digests are verified on login.
2. **Role-Based Authorization:** Endpoints verify whether the caller has `BUYER`, `SELLER`, or `ADMIN` authority.
3. **Product Ownership Enforcement:** Sellers cannot alter or delete products belonging to other merchants.
4. **Server-Side Price Calculation:** The server queries database prices during checkout rather than trusting client-side price submissions.
5. **Database Transactions:** Order creation and stock reduction run within an atomic `@Transactional` boundary, guaranteeing that inventory counts remain accurate.

---

## 🎓 Capstone Viva / Evaluation Q&A

**Q: How does the application store data?**  
A: RuthraMart uses an embedded persistent H2 file database (`./data/ruthramart`) configured in MySQL compatibility mode. It persists data across server restarts and runs instantly on any machine without installing MySQL or Docker. It also supports switching to a MySQL server via `SPRING_DATASOURCE_URL`.

**Q: How does the AI Chatbot work without an API key?**  
A: `ChatbotService` contains an intelligent, database-aware conversational engine. When a user asks about laptops, categories, or order tracking, the bot inspects live inventory from `ProductRepository` and replies with accurate items and prices. If `GEMINI_API_KEY` is provided, it can also query Google Gemini.

**Q: How is inventory concurrency handled?**  
A: During `POST /api/orders`, the server locks inventory within a transactional block, verifies stock availability, deducts the requested quantities, and clears the user's cart in a single atomic database operation. If an order is subsequently cancelled, stock is restored automatically.
