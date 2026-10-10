package com.ruthramart.RuthraMart.controller;

import com.ruthramart.RuthraMart.model.CartItem;
import com.ruthramart.RuthraMart.model.Product;
import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.CartItemRepository;
import com.ruthramart.RuthraMart.repository.ProductRepository;
import com.ruthramart.RuthraMart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public CartController(CartItemRepository cartItemRepository, ProductRepository productRepository, AuthService authService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please log in to view your cart."));
        }
        User user = userOpt.get();

        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        double totalAmount = 0.0;
        int totalItems = 0;

        List<Map<String, Object>> responseItems = new ArrayList<>();
        for (CartItem item : items) {
            Product p = item.getProduct();
            double subtotal = p.getPrice() * item.getQuantity();
            totalAmount += subtotal;
            totalItems += item.getQuantity();

            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("productId", p.getId());
            map.put("name", p.getName());
            map.put("category", p.getCategory());
            map.put("price", p.getPrice());
            map.put("imageUrl", p.getImageUrl());
            map.put("stockQuantity", p.getStockQuantity());
            map.put("quantity", item.getQuantity());
            map.put("subtotal", subtotal);
            responseItems.add(map);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", responseItems);
        response.put("totalAmount", totalAmount);
        response.put("totalItems", totalItems);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please log in to add items to your cart."));
        }
        User user = userOpt.get();

        Long productId;
        int quantity = 1;
        try {
            productId = Long.parseLong(String.valueOf(body.get("productId")));
            if (body.containsKey("quantity")) {
                quantity = Integer.parseInt(String.valueOf(body.get("quantity")));
            }
            if (quantity <= 0) quantity = 1;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid product ID or quantity."));
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Product not found."));
        }

        Product product = productOpt.get();
        Optional<CartItem> existingOpt = cartItemRepository.findByUserIdAndProductId(user.getId(), productId);

        int newQuantity = quantity;
        if (existingOpt.isPresent()) {
            newQuantity += existingOpt.get().getQuantity();
        }

        if (newQuantity > product.getStockQuantity()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Insufficient stock available. Only " + product.getStockQuantity() + " units in stock."
            ));
        }

        CartItem item;
        if (existingOpt.isPresent()) {
            item = existingOpt.get();
            item.setQuantity(newQuantity);
        } else {
            item = new CartItem(user.getId(), product, quantity);
        }

        cartItemRepository.save(item);
        return ResponseEntity.ok(Map.of("success", true, "message", "Product added to cart successfully!"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<CartItem> itemOpt = cartItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = itemOpt.get();
        if (!item.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
        }

        int quantity;
        try {
            quantity = Integer.parseInt(String.valueOf(body.get("quantity")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid quantity."));
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return ResponseEntity.ok(Map.of("success", true, "message", "Item removed from cart."));
        }

        Product product = item.getProduct();
        if (quantity > product.getStockQuantity()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Insufficient stock available. Maximum available is " + product.getStockQuantity() + "."
            ));
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return ResponseEntity.ok(Map.of("success", true, "message", "Cart quantity updated successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<CartItem> itemOpt = cartItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = itemOpt.get();
        if (!item.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
        }

        cartItemRepository.delete(item);
        return ResponseEntity.ok(Map.of("success", true, "message", "Item removed from cart."));
    }

    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        cartItemRepository.deleteByUserId(userOpt.get().getId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Cart cleared successfully."));
    }
}
