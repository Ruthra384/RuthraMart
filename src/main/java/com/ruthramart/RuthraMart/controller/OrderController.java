package com.ruthramart.RuthraMart.controller;

import com.ruthramart.RuthraMart.model.CartItem;
import com.ruthramart.RuthraMart.model.Order;
import com.ruthramart.RuthraMart.model.OrderItem;
import com.ruthramart.RuthraMart.model.Product;
import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.CartItemRepository;
import com.ruthramart.RuthraMart.repository.OrderItemRepository;
import com.ruthramart.RuthraMart.repository.OrderRepository;
import com.ruthramart.RuthraMart.repository.ProductRepository;
import com.ruthramart.RuthraMart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public OrderController(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           AuthService authService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authService = authService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please log in to place an order."));
        }
        User user = userOpt.get();

        String buyerName = (String) body.get("buyerName");
        String email = (String) body.get("email");
        String phone = (String) body.get("phone");
        String deliveryAddress = (String) body.get("deliveryAddress");

        if (buyerName == null || buyerName.isBlank()) buyerName = user.getName();
        if (email == null || email.isBlank()) email = user.getEmail();
        if (phone == null || phone.isBlank()) phone = user.getPhone();

        if (deliveryAddress == null || deliveryAddress.trim().length() < 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "A valid delivery address is required."));
        }

        // Fetch current user's cart items
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Your cart is empty."));
        }

        // Validate stock for each product first
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            if (p.getStockQuantity() < ci.getQuantity()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Insufficient stock available for '" + p.getName() + "'. Available: " + p.getStockQuantity()
                ));
            }
        }

        // Generate unique order ID e.g. RTH10001
        long orderCount = orderRepository.count();
        String orderNumber = "RTH" + (10001 + orderCount);

        double totalAmount = 0.0;
        Order order = new Order(
                orderNumber,
                user.getId(),
                buyerName,
                email,
                phone,
                deliveryAddress.trim(),
                0.0
        );

        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            double itemSubtotal = p.getPrice() * ci.getQuantity();
            totalAmount += itemSubtotal;

            // Reduce stock
            p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
            productRepository.save(p);

            OrderItem orderItem = new OrderItem(
                    order,
                    p.getId(),
                    p.getName(),
                    p.getSellerId(),
                    p.getPrice(),
                    ci.getQuantity(),
                    itemSubtotal,
                    p.getImageUrl()
            );
            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteByUserId(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Order placed successfully!");
        response.put("orderId", savedOrder.getId());
        response.put("orderNumber", savedOrder.getOrderNumber());
        response.put("totalAmount", savedOrder.getTotalAmount());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok(orderRepository.findAllByOrderByCreatedAtDesc());
        } else if ("SELLER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.ok(orderItemRepository.findOrdersBySellerId(user.getId()));
        } else {
            // Buyer
            return ResponseEntity.ok(orderRepository.findByBuyerIdOrderByCreatedAtDesc(user.getId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        // Permission check
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) &&
            !order.getBuyerId().equals(user.getId())) {
            // Check if seller has items in this order
            boolean isSellerInOrder = order.getItems().stream().anyMatch(i -> i.getSellerId().equals(user.getId()));
            if (!isSellerInOrder) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
            }
        }

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required."));
        }
        User user = userOpt.get();

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status is required."));
        }

        List<String> validStatuses = List.of("Pending", "Confirmed", "Shipped", "Delivered", "Cancelled");
        if (!validStatuses.contains(newStatus)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid order status. Allowed: " + validStatuses));
        }

        // Authorization check
        if ("BUYER".equalsIgnoreCase(user.getRole())) {
            // Buyer can only cancel their own pending order
            if (!order.getBuyerId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied."));
            }
            if (!"Cancelled".equalsIgnoreCase(newStatus)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Buyers can only cancel pending orders."));
            }
            if (!"Pending".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Only pending orders can be cancelled."));
            }
        }

        // If order is cancelled, restore stock
        if ("Cancelled".equalsIgnoreCase(newStatus) && !"Cancelled".equalsIgnoreCase(order.getStatus())) {
            for (OrderItem item : order.getItems()) {
                productRepository.findById(item.getProductId()).ifPresent(p -> {
                    p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                    productRepository.save(p);
                });
            }
        }

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order status updated to " + newStatus,
                "order", updated
        ));
    }
}
