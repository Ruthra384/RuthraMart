package com.ruthramart.RuthraMart.config;

import com.ruthramart.RuthraMart.model.*;
import com.ruthramart.RuthraMart.repository.*;
import com.ruthramart.RuthraMart.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategories();
        seedProducts();
        seedOrders();
        seedReviews();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // 1 Admin
            User admin = new User(
                    "Ruthra Admin",
                    "admin@ruthramart.com",
                    "9876543210",
                    PasswordUtil.hashPassword("Admin@123"),
                    "ADMIN"
            );
            userRepository.save(admin);

            // 2 Sellers
            User seller1 = new User(
                    "TechZone Electronics",
                    "seller1@ruthramart.com",
                    "9876543211",
                    PasswordUtil.hashPassword("Seller@123"),
                    "SELLER"
            );
            userRepository.save(seller1);

            User seller2 = new User(
                    "Elite Trendz & Living",
                    "seller2@ruthramart.com",
                    "9876543212",
                    PasswordUtil.hashPassword("Seller@123"),
                    "SELLER"
            );
            userRepository.save(seller2);

            // 3 Buyers
            User buyer1 = new User(
                    "Arun Kumar",
                    "buyer1@ruthramart.com",
                    "9876543213",
                    PasswordUtil.hashPassword("Buyer@123"),
                    "BUYER"
            );
            userRepository.save(buyer1);

            User buyer2 = new User(
                    "Priya Sharma",
                    "buyer2@ruthramart.com",
                    "9876543214",
                    PasswordUtil.hashPassword("Buyer@123"),
                    "BUYER"
            );
            userRepository.save(buyer2);

            User buyer3 = new User(
                    "Kavitha R",
                    "buyer3@ruthramart.com",
                    "9876543215",
                    PasswordUtil.hashPassword("Buyer@123"),
                    "BUYER"
            );
            userRepository.save(buyer3);
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Electronics", "Laptops, mobiles, audio devices, and smart tech", "fa-laptop"));
            categoryRepository.save(new Category("Fashion", "Premium ethnic wear, casual attire, and modern styles", "fa-shirt"));
            categoryRepository.save(new Category("Accessories", "Watches, handcrafted handbags, and daily essentials", "fa-gem"));
            categoryRepository.save(new Category("Home", "Artistic vases, calming lights, and aesthetic decor", "fa-couch"));
            categoryRepository.save(new Category("Beauty", "Herbal skincare, organic serums, and wellness", "fa-spa"));
            categoryRepository.save(new Category("Books", "Best-selling technical guides, psychology, and mindset", "fa-book-open"));
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            User seller1 = userRepository.findByEmail("seller1@ruthramart.com").orElse(null);
            User seller2 = userRepository.findByEmail("seller2@ruthramart.com").orElse(null);

            Long s1Id = seller1 != null ? seller1.getId() : 2L;
            String s1Name = seller1 != null ? seller1.getName() : "TechZone Electronics";

            Long s2Id = seller2 != null ? seller2.getId() : 3L;
            String s2Name = seller2 != null ? seller2.getName() : "Elite Trendz & Living";

            // Electronics & Accessories by Seller 1
            productRepository.save(new Product(
                    "Laptop",
                    "High-performance Intel Core i7 16GB RAM 512GB SSD 15.6-inch FHD anti-glare display laptop designed for seamless coding, multitasking, and productivity.",
                    "Electronics",
                    45000.0,
                    15,
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            productRepository.save(new Product(
                    "Mobile",
                    "Next-generation 5G smartphone with 6.5-inch AMOLED 120Hz display, 64MP triple camera, 5000mAh all-day battery, and fast charging.",
                    "Electronics",
                    18000.0,
                    25,
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            productRepository.save(new Product(
                    "Headphones",
                    "Wireless Bluetooth over-ear headphones with active noise cancellation, deep bass acoustic tuning, and up to 40 hours playtime.",
                    "Electronics",
                    999.0,
                    50,
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            productRepository.save(new Product(
                    "Smart Watch",
                    "Water-resistant smartwatch featuring continuous heart rate tracking, SpO2 sensor, sleep monitoring, customizable watch faces, and 10-day battery life.",
                    "Accessories",
                    1999.0,
                    30,
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            // Fashion & Accessories by Seller 2
            productRepository.save(new Product(
                    "Handbag",
                    "Handcrafted premium vegan leather tote handbag with spacious compartments, durable brass hardware, and minimalist aesthetic design.",
                    "Accessories",
                    1499.0,
                    20,
                    "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80",
                    s2Id,
                    s2Name
            ));

            productRepository.save(new Product(
                    "Pure Cotton Printed Kurti Set",
                    "Breathable soft pure cotton ethnic printed kurti paired with matching palazzos. Elegant sage tones perfect for celebrations and office wear.",
                    "Fashion",
                    1299.0,
                    40,
                    "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&auto=format&fit=crop&q=80",
                    s2Id,
                    s2Name
            ));

            productRepository.save(new Product(
                    "Men's Casual Linen Shirt",
                    "Breathable relaxed-fit pure linen shirt with mandarin collar and mother-of-pearl buttons in soothing olive sage.",
                    "Fashion",
                    1199.0,
                    35,
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop&q=80",
                    s2Id,
                    s2Name
            ));

            productRepository.save(new Product(
                    "Artisan Ceramic Table Vase",
                    "Handmade stoneware ceramic vase with subtle textured beige matte glaze. Complements modern botanical stems and dried florals.",
                    "Home",
                    899.0,
                    25,
                    "https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?w=600&auto=format&fit=crop&q=80",
                    s2Id,
                    s2Name
            ));

            productRepository.save(new Product(
                    "Minimalist LED Desk Lamp",
                    "Dimmable warm-light desk lamp with flexible silicone gooseneck, touch brightness sensor, and integrated eye-care diffuser.",
                    "Home",
                    1499.0,
                    22,
                    "https://images.unsplash.com/photo-1534073828943-f801091bb18c?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            productRepository.save(new Product(
                    "Organic Herbal Glow Serum",
                    "Natural restorative facial serum enriched with cold-pressed rosehip seed oil, vitamin C, and soothing chamomile extract.",
                    "Beauty",
                    699.0,
                    60,
                    "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=600&auto=format&fit=crop&q=80",
                    s2Id,
                    s2Name
            ));

            productRepository.save(new Product(
                    "Clean Code: A Handbook of Agile Software",
                    "Essential classic engineering guide by Robert C. Martin detailing principles, patterns, and refactoring techniques for high quality software.",
                    "Books",
                    1199.0,
                    18,
                    "https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));

            productRepository.save(new Product(
                    "The Psychology of Money",
                    "Timeless lessons on wealth, greed, and happiness by Morgan Housel showing how behavior and mindset determine financial success.",
                    "Books",
                    399.0,
                    45,
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80",
                    s1Id,
                    s1Name
            ));
        }
    }

    private void seedOrders() {
        if (orderRepository.count() == 0) {
            User buyer1 = userRepository.findByEmail("buyer1@ruthramart.com").orElse(null);
            User buyer2 = userRepository.findByEmail("buyer2@ruthramart.com").orElse(null);
            List<Product> products = productRepository.findAll();

            if (buyer1 != null && products.size() >= 3) {
                Product laptop = products.get(0);
                Product headphones = products.get(2);

                Order order1 = new Order(
                        "RTH10001",
                        buyer1.getId(),
                        buyer1.getName(),
                        buyer1.getEmail(),
                        buyer1.getPhone(),
                        "No. 42, Anna Nagar West, Chennai, Tamil Nadu - 600040",
                        laptop.getPrice() + headphones.getPrice()
                );
                order1.setStatus("Delivered");

                OrderItem item1 = new OrderItem(order1, laptop.getId(), laptop.getName(), laptop.getSellerId(), laptop.getPrice(), 1, laptop.getPrice(), laptop.getImageUrl());
                OrderItem item2 = new OrderItem(order1, headphones.getId(), headphones.getName(), headphones.getSellerId(), headphones.getPrice(), 1, headphones.getPrice(), headphones.getImageUrl());
                order1.addItem(item1);
                order1.addItem(item2);
                orderRepository.save(order1);
            }

            if (buyer2 != null && products.size() >= 5) {
                Product watch = products.get(3);
                Product handbag = products.get(4);

                Order order2 = new Order(
                        "RTH10002",
                        buyer2.getId(),
                        buyer2.getName(),
                        buyer2.getEmail(),
                        buyer2.getPhone(),
                        "Flat 302, Green Meadows, Indiranagar, Bangalore, Karnataka - 560038",
                        watch.getPrice() + handbag.getPrice()
                );
                order2.setStatus("Shipped");

                OrderItem item1 = new OrderItem(order2, watch.getId(), watch.getName(), watch.getSellerId(), watch.getPrice(), 1, watch.getPrice(), watch.getImageUrl());
                OrderItem item2 = new OrderItem(order2, handbag.getId(), handbag.getName(), handbag.getSellerId(), handbag.getPrice(), 1, handbag.getPrice(), handbag.getImageUrl());
                order2.addItem(item1);
                order2.addItem(item2);
                orderRepository.save(order2);
            }
        }
    }

    private void seedReviews() {
        if (reviewRepository.count() == 0) {
            List<Product> products = productRepository.findAll();
            User buyer1 = userRepository.findByEmail("buyer1@ruthramart.com").orElse(null);
            User buyer2 = userRepository.findByEmail("buyer2@ruthramart.com").orElse(null);
            User buyer3 = userRepository.findByEmail("buyer3@ruthramart.com").orElse(null);

            if (!products.isEmpty() && buyer1 != null) {
                Product laptop = products.get(0);
                reviewRepository.save(new Review(laptop.getId(), buyer1.getId(), buyer1.getName(), 5,
                        "Outstanding performance! Boots up instantly, handles multi-tab browsing and development IDEs effortlessly. Highly recommended for students and professionals."));

                if (products.size() > 2) {
                    Product headphones = products.get(2);
                    reviewRepository.save(new Review(headphones.getId(), buyer1.getId(), buyer1.getName(), 4,
                            "Exceptional clarity and soundstage. The noise cancellation is impressive for this price range. Very comfortable padding."));
                }
            }

            if (products.size() > 3 && buyer2 != null) {
                Product watch = products.get(3);
                reviewRepository.save(new Review(watch.getId(), buyer2.getId(), buyer2.getName(), 5,
                        "Aesthetic design with very accurate sleep and step tracking. Battery lasts well over a week on a single charge."));

                if (products.size() > 4) {
                    Product handbag = products.get(4);
                    reviewRepository.save(new Review(handbag.getId(), buyer2.getId(), buyer2.getName(), 5,
                            "The vegan leather finish is top tier! Fits my tablet, wallet, and keys with plenty of room to spare. Lovely neutral colour!"));
                }
            }

            if (products.size() > 10 && buyer3 != null) {
                Product book = products.get(10);
                reviewRepository.save(new Review(book.getId(), buyer3.getId(), buyer3.getName(), 5,
                        "An absolute classic for writing clean, readable, and maintainable software. Changed my entire approach to coding!"));
            }
        }
    }
}
