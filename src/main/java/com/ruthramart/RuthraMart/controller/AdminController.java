package com.ruthramart.RuthraMart.controller;

import com.ruthramart.RuthraMart.model.Product;
import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.OrderRepository;
import com.ruthramart.RuthraMart.repository.ProductRepository;
import com.ruthramart.RuthraMart.repository.UserRepository;
import com.ruthramart.RuthraMart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;

    public AdminController(UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           AuthService authService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    private boolean checkAdmin(HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        return userOpt.isPresent() && "ADMIN".equalsIgnoreCase(userOpt.get().getRole());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required."));
        }

        long totalUsers = userRepository.count();
        long totalBuyers = userRepository.countByRole("BUYER");
        long totalSellers = userRepository.countByRole("SELLER");
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        Double totalSales = orderRepository.calculateTotalSales();
        if (totalSales == null) totalSales = 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalBuyers", totalBuyers);
        stats.put("totalSellers", totalSellers);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("totalSales", totalSales);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required."));
        }

        List<User> users = userRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("role", u.getRole());
            map.put("createdAt", u.getCreatedAt());
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required."));
        }
        return ResponseEntity.ok(productRepository.findAll());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> removeProduct(@PathVariable Long id, HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required."));
        }

        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productRepository.delete(productOpt.get());
        return ResponseEntity.ok(Map.of("success", true, "message", "Product removed successfully by administrator."));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required."));
        }
        return ResponseEntity.ok(orderRepository.findAllByOrderByCreatedAtDesc());
    }
}
