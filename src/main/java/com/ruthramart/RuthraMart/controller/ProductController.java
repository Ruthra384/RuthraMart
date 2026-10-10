package com.ruthramart.RuthraMart.controller;

import com.ruthramart.RuthraMart.model.Product;
import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.ProductRepository;
import com.ruthramart.RuthraMart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final AuthService authService;

    public ProductController(ProductRepository productRepository, AuthService authService) {
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {

        if ((category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) ||
            (query != null && !query.isBlank())) {
            String cat = (category != null && !category.equalsIgnoreCase("All")) ? category.trim() : null;
            String q = (query != null && !query.isBlank()) ? query.trim() : null;
            return ResponseEntity.ok(productRepository.filterAndSearch(cat, q));
        }

        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/seller")
    public ResponseEntity<?> getSellerProducts(HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();
        if (!"SELLER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Seller access only."));
        }
        return ResponseEntity.ok(productRepository.findBySellerId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();
        if (!"SELLER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Only sellers and admins can add products."));
        }

        String name = (String) body.get("name");
        String description = (String) body.get("description");
        String category = (String) body.get("category");
        String imageUrl = (String) body.get("imageUrl");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Product name is required."));
        }
        if (category == null || category.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Category is required."));
        }

        double price;
        try {
            price = Double.parseDouble(String.valueOf(body.get("price")));
            if (price <= 0) throw new IllegalArgumentException();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Price must be a positive number."));
        }

        int stock;
        try {
            stock = Integer.parseInt(String.valueOf(body.get("stockQuantity")));
            if (stock < 0) throw new IllegalArgumentException();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Stock quantity must be zero or greater."));
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80";
        }

        Product product = new Product(
                name.trim(),
                description != null ? description.trim() : "",
                category.trim(),
                price,
                stock,
                imageUrl.trim(),
                user.getId(),
                user.getName()
        );

        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOpt.get();

        // Enforce ownership: only the seller who created the product or an ADMIN can update it
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !product.getSellerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You can only edit your own products."));
        }

        if (body.containsKey("name")) product.setName((String) body.get("name"));
        if (body.containsKey("description")) product.setDescription((String) body.get("description"));
        if (body.containsKey("category")) product.setCategory((String) body.get("category"));
        if (body.containsKey("imageUrl")) product.setImageUrl((String) body.get("imageUrl"));

        if (body.containsKey("price")) {
            try {
                double p = Double.parseDouble(String.valueOf(body.get("price")));
                if (p > 0) product.setPrice(p);
            } catch (Exception ignored) {}
        }

        if (body.containsKey("stockQuantity")) {
            try {
                int s = Integer.parseInt(String.valueOf(body.get("stockQuantity")));
                if (s >= 0) product.setStockQuantity(s);
            } catch (Exception ignored) {}
        }

        Product updated = productRepository.save(product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productOpt.get();

        // Enforce ownership: only the seller who created the product or an ADMIN can delete it
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !product.getSellerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You can only delete your own products."));
        }

        productRepository.delete(product);
        return ResponseEntity.ok(Map.of("success", true, "message", "Product deleted successfully."));
    }
}
