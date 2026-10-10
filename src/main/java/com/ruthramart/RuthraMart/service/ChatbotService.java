package com.ruthramart.RuthraMart.service;

import com.ruthramart.RuthraMart.model.Category;
import com.ruthramart.RuthraMart.model.Product;
import com.ruthramart.RuthraMart.repository.CategoryRepository;
import com.ruthramart.RuthraMart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Service
public class ChatbotService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ChatbotService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public String generateResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! I am RuthraMart AI Assistant. How can I help you today?";
        }

        String prompt = userMessage.trim();
        String lower = prompt.toLowerCase(Locale.ROOT);

        // Check for external AI API key if configured
        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                String aiReply = callGeminiApi(geminiApiKey, prompt);
                if (aiReply != null && !aiReply.isBlank()) {
                    return aiReply;
                }
            } catch (Exception ignored) {
                // Fallback to internal rule-based AI engine seamlessly
            }
        }

        // INTELLIGENT RULE-BASED AND INVENTORY-AWARE ASSISTANT
        return processRuleBasedResponse(lower, prompt);
    }

    private String processRuleBasedResponse(String lower, String original) {
        // Greetings
        if (lower.matches("^(hi|hello|hey|namaste|greetings).*") || lower.equals("hi") || lower.equals("hello")) {
            return "Hello! Welcome to RuthraMart! 🌿 How can I help you today? You can ask me to search products, guide you through categories, assist with cart & orders, or answer shopping FAQs.";
        }

        // Identity / Who are you
        if (lower.contains("who are you") || lower.contains("what is ruthramart") || lower.contains("your name")) {
            return "I am the RuthraMart AI Assistant! RuthraMart is a multi-seller online shopping platform where you can discover thoughtful finds for everyday living across Electronics, Fashion, Home, Beauty, Accessories, and Books.";
        }

        // Cart Guidance
        if (lower.contains("add to cart") || (lower.contains("cart") && (lower.contains("how") || lower.contains("add")))) {
            return "To add products to your cart:\n1. Browse our Products or Categories.\n2. Click the 'Add to Cart' button on any product card.\n3. Open the 'Cart' link in the top navigation bar to adjust quantities and review your subtotal!";
        }

        // View Cart
        if (lower.contains("where is my cart") || lower.contains("view cart") || lower.contains("open cart")) {
            return "You can view your shopping cart anytime by clicking the 'Cart' link on the top navigation bar.";
        }

        // Order & Tracking Guidance
        if (lower.contains("see my order") || lower.contains("track order") || lower.contains("view order") || lower.contains("order history") || lower.contains("order status")) {
            return "To view your orders:\n1. Log in to your Buyer account.\n2. Click 'Orders' in the navigation bar.\n3. You will see your complete order history, Order IDs (e.g., RTH10001), item details, and live status (Pending, Confirmed, Shipped, Delivered).";
        }

        // Checkout guidance
        if (lower.contains("how to checkout") || lower.contains("checkout") || lower.contains("place order") || lower.contains("buy")) {
            if (!lower.contains("laptop") && !lower.contains("mobile") && !lower.contains("phone") && !lower.contains("watch") && !lower.contains("bag")) {
                return "To complete your purchase:\n1. Add your desired items to the Cart.\n2. Go to the Cart page and click 'Proceed to Checkout'.\n3. Enter your Delivery Address, Buyer Name, and Phone Number.\n4. Click 'Confirm Order' to place your order with an instant Order ID!";
            }
        }

        // Categories inquiry
        if (lower.contains("category") || lower.contains("categories") || lower.contains("what do you sell") || lower.contains("departments")) {
            List<Category> categories = categoryRepository.findAll();
            StringBuilder sb = new StringBuilder("RuthraMart offers the following curated categories:\n");
            for (Category c : categories) {
                sb.append("• ").append(c.getName()).append(" - ").append(c.getDescription() != null ? c.getDescription() : "").append("\n");
            }
            sb.append("\nYou can click 'Categories' in the navbar to filter products instantly!");
            return sb.toString();
        }

        // Seller guidance
        if (lower.contains("sell") || lower.contains("become a seller") || lower.contains("seller dashboard")) {
            return "Interested in selling on RuthraMart?\n1. Register a new account and choose the 'Seller' role.\n2. Log in to access your dedicated Seller Dashboard.\n3. Add your products, set stock quantities, manage prices, and fulfill orders received from buyers!";
        }

        // Admin guidance
        if (lower.contains("admin") || lower.contains("administrator")) {
            return "The Admin portal allows authorized administrators to manage buyers, sellers, products, and overall store orders via the Admin Dashboard.";
        }

        // Delivery / Shipping
        if (lower.contains("delivery") || lower.contains("shipping") || lower.contains("how long")) {
            return "RuthraMart offers reliable standard delivery within 2 to 4 business days. You can track your shipment status from Pending to Shipped and Delivered directly in your Orders page.";
        }

        // Return / Refund policy
        if (lower.contains("return") || lower.contains("refund") || lower.contains("cancellation") || lower.contains("cancel")) {
            return "We have a customer-friendly 7-day return policy! You can also cancel any pending order directly from your Orders page, and reserved stock will automatically be restored.";
        }

        // Payment methods
        if (lower.contains("payment") || lower.contains("cod") || lower.contains("upi") || lower.contains("pay")) {
            return "RuthraMart supports Cash on Delivery (COD), UPI (Google Pay, PhonePe, Paytm), Credit/Debit Cards, and Net Banking simulation for effortless order confirmation.";
        }

        // Reviews & Ratings
        if (lower.contains("review") || lower.contains("rating") || lower.contains("rate")) {
            return "Buyers can leave 1 to 5 star ratings and honest written reviews for products. Simply open the Product Details page to read community feedback and submit your own!";
        }

        // Specific Product / Keyword Search in Database
        String searchKeyword = extractSearchKeyword(lower);
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            List<Product> matches = productRepository.searchProducts(searchKeyword);
            if (!matches.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Here are some matching products I found for '").append(searchKeyword).append("':\n\n");
                int limit = Math.min(matches.size(), 4);
                for (int i = 0; i < limit; i++) {
                    Product p = matches.get(i);
                    sb.append("✨ ").append(p.getName())
                      .append(" — ₹").append(String.format("%,.0f", p.getPrice()))
                      .append(" (Category: ").append(p.getCategory())
                      .append(", In Stock: ").append(p.getStockQuantity()).append(")\n");
                }
                sb.append("\nYou can find and add them to your cart directly from the Products page!");
                return sb.toString();
            }
        }

        // Fallback search across all products if user typed something specific
        List<Product> generalMatches = productRepository.searchProducts(lower.replace("i need", "").replace("i want", "").replace("show me", "").trim());
        if (!generalMatches.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("I found the following items matching your search:\n\n");
            int limit = Math.min(generalMatches.size(), 3);
            for (int i = 0; i < limit; i++) {
                Product p = generalMatches.get(i);
                sb.append("• ").append(p.getName()).append(" - ₹").append(String.format("%,.0f", p.getPrice())).append("\n");
            }
            sb.append("\nFeel free to explore them on our Products page!");
            return sb.toString();
        }

        // General fallback
        return "I'm here to assist you! You can ask me to search for items like 'laptop', 'mobile', 'watch', 'handbag', or ask about 'categories', 'cart', 'order status', and 'seller registration'.";
    }

    private String extractSearchKeyword(String lower) {
        String[] keywords = {"laptop", "mobile", "phone", "headphone", "watch", "handbag", "bag", "kurti", "shirt", "vase", "lamp", "serum", "clean code", "book", "money"};
        for (String kw : keywords) {
            if (lower.contains(kw)) {
                return kw;
            }
        }
        return null;
    }

    private String callGeminiApi(String apiKey, String userPrompt) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(5000);

            String jsonPayload = "{\"contents\": [{\"parts\": [{\"text\": \"You are RuthraMart AI Assistant for RuthraMart e-commerce website. Answer briefly and politely: " 
                + userPrompt.replace("\"", "\\\"") + "\"}]}]}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    String response = scanner.useDelimiter("\\A").next();
                    // Basic extraction of text field from Gemini JSON
                    int textIndex = response.indexOf("\"text\": \"");
                    if (textIndex != -1) {
                        int start = textIndex + 9;
                        int end = response.indexOf("\"", start);
                        if (end != -1) {
                            return response.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore and fallback
        }
        return null;
    }
}
