-- ============================================================
-- RUTHRAMART SEED DATA
-- Demonstration Data for Capstone Project Evaluation
-- ============================================================

-- 1. Users (1 Admin, 2 Sellers, 3 Buyers)
-- Passwords hashed using SHA-256 + RuthraMart_Secure_Salt_2026:
-- Admin@123 -> a2b66ca210775d7bfe7b165b6f34584e0303ba65da9f62c8faea8536ec7a7315 (example)
INSERT INTO users (id, name, email, phone, password, role) VALUES
(1, 'Ruthra Admin', 'admin@ruthramart.com', '9876543210', '0289a421b44d3202fbff6bba17f8b2d1d0eeeb2ea19d850ec2522c06927956fa', 'ADMIN'),
(2, 'TechZone Electronics', 'seller1@ruthramart.com', '9876543211', '8088be3fa66dfab04cb3d3c8c763ebc8397a61d6bc08183bb9db1d6365fa363b', 'SELLER'),
(3, 'Elite Trendz & Living', 'seller2@ruthramart.com', '9876543212', '8088be3fa66dfab04cb3d3c8c763ebc8397a61d6bc08183bb9db1d6365fa363b', 'SELLER'),
(4, 'Arun Kumar', 'buyer1@ruthramart.com', '9876543213', '10ce69bf23253a6f1ea60ce01783ae410887df92fe98db253fc5d9b54e3d9370', 'BUYER'),
(5, 'Priya Sharma', 'buyer2@ruthramart.com', '9876543214', '10ce69bf23253a6f1ea60ce01783ae410887df92fe98db253fc5d9b54e3d9370', 'BUYER'),
(6, 'Kavitha R', 'buyer3@ruthramart.com', '9876543215', '10ce69bf23253a6f1ea60ce01783ae410887df92fe98db253fc5d9b54e3d9370', 'BUYER');

-- 2. Categories
INSERT INTO categories (id, name, description, icon) VALUES
(1, 'Electronics', 'Laptops, mobiles, audio devices, and smart tech', 'fa-laptop'),
(2, 'Fashion', 'Premium ethnic wear, casual attire, and modern styles', 'fa-shirt'),
(3, 'Accessories', 'Watches, handcrafted handbags, and daily essentials', 'fa-gem'),
(4, 'Home', 'Artistic vases, calming lights, and aesthetic decor', 'fa-couch'),
(5, 'Beauty', 'Herbal skincare, organic serums, and wellness', 'fa-spa'),
(6, 'Books', 'Best-selling technical guides, psychology, and mindset', 'fa-book-open');

-- 3. Products
INSERT INTO products (id, name, description, category, price, stock_quantity, image_url, seller_id, seller_name) VALUES
(1, 'Laptop', 'High-performance Intel Core i7 16GB RAM 512GB SSD 15.6-inch FHD anti-glare display laptop designed for seamless coding, multitasking, and productivity.', 'Electronics', 45000.00, 15, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(2, 'Mobile', 'Next-generation 5G smartphone with 6.5-inch AMOLED 120Hz display, 64MP triple camera, 5000mAh all-day battery, and fast charging.', 'Electronics', 18000.00, 25, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(3, 'Headphones', 'Wireless Bluetooth over-ear headphones with active noise cancellation, deep bass acoustic tuning, and up to 40 hours playtime.', 'Electronics', 999.00, 50, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(4, 'Smart Watch', 'Water-resistant smartwatch featuring continuous heart rate tracking, SpO2 sensor, sleep monitoring, customizable watch faces, and 10-day battery life.', 'Accessories', 1999.00, 30, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(5, 'Handbag', 'Handcrafted premium vegan leather tote handbag with spacious compartments, durable brass hardware, and minimalist aesthetic design.', 'Accessories', 1499.00, 20, 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80', 3, 'Elite Trendz & Living'),
(6, 'Pure Cotton Printed Kurti Set', 'Breathable soft pure cotton ethnic printed kurti paired with matching palazzos in soothing olive sage tones.', 'Fashion', 1299.00, 40, 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&auto=format&fit=crop&q=80', 3, 'Elite Trendz & Living'),
(7, 'Men''s Casual Linen Shirt', 'Breathable relaxed-fit pure linen shirt with mandarin collar and mother-of-pearl buttons in soothing olive sage.', 'Fashion', 1199.00, 35, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop&q=80', 3, 'Elite Trendz & Living'),
(8, 'Artisan Ceramic Table Vase', 'Handmade stoneware ceramic vase with subtle textured beige matte glaze. Complements modern botanical stems.', 'Home', 899.00, 25, 'https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?w=600&auto=format&fit=crop&q=80', 3, 'Elite Trendz & Living'),
(9, 'Minimalist LED Desk Lamp', 'Dimmable warm-light desk lamp with flexible silicone gooseneck, touch brightness sensor, and eye-care diffuser.', 'Home', 1499.00, 22, 'https://images.unsplash.com/photo-1534073828943-f801091bb18c?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(10, 'Organic Herbal Glow Serum', 'Natural restorative facial serum enriched with cold-pressed rosehip seed oil, vitamin C, and soothing chamomile extract.', 'Beauty', 699.00, 60, 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop&q=80', 3, 'Elite Trendz & Living'),
(11, 'Clean Code: A Handbook of Agile Software', 'Essential classic engineering guide by Robert C. Martin detailing principles, patterns, and refactoring techniques.', 'Books', 1199.00, 18, 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics'),
(12, 'The Psychology of Money', 'Timeless lessons on wealth, greed, and happiness by Morgan Housel showing how behavior and mindset determine financial success.', 'Books', 399.00, 45, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80', 2, 'TechZone Electronics');

-- 4. Sample Orders
INSERT INTO orders (id, order_number, buyer_id, buyer_name, email, phone, delivery_address, total_amount, status) VALUES
(1, 'RTH10001', 4, 'Arun Kumar', 'buyer1@ruthramart.com', '9876543213', 'No. 42, Anna Nagar West, Chennai, Tamil Nadu - 600040', 45999.00, 'Delivered'),
(2, 'RTH10002', 5, 'Priya Sharma', 'buyer2@ruthramart.com', '9876543214', 'Flat 302, Green Meadows, Indiranagar, Bangalore, Karnataka - 560038', 3498.00, 'Shipped');

-- 5. Order Items
INSERT INTO order_items (id, order_id, product_id, product_name, seller_id, price, quantity, subtotal, image_url) VALUES
(1, 1, 1, 'Laptop', 2, 45000.00, 1, 45000.00, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&auto=format&fit=crop&q=80'),
(2, 1, 3, 'Headphones', 2, 999.00, 1, 999.00, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80'),
(3, 2, 4, 'Smart Watch', 2, 1999.00, 1, 1999.00, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80'),
(4, 2, 5, 'Handbag', 3, 1499.00, 1, 1499.00, 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80');

-- 6. Sample Reviews
INSERT INTO reviews (id, product_id, buyer_id, buyer_name, rating, comment) VALUES
(1, 1, 4, 'Arun Kumar', 5, 'Outstanding performance! Boots up instantly, handles multi-tab browsing and development IDEs effortlessly. Highly recommended for students and professionals.'),
(2, 3, 4, 'Arun Kumar', 4, 'Exceptional clarity and soundstage. The noise cancellation is impressive for this price range. Very comfortable padding.'),
(3, 4, 5, 'Priya Sharma', 5, 'Aesthetic design with very accurate sleep and step tracking. Battery lasts well over a week on a single charge.'),
(4, 5, 5, 'Priya Sharma', 5, 'The vegan leather finish is top tier! Fits my tablet, wallet, and keys with plenty of room to spare. Lovely neutral colour!'),
(5, 11, 6, 'Kavitha R', 5, 'An absolute classic for writing clean, readable, and maintainable software. Changed my entire approach to coding!');
