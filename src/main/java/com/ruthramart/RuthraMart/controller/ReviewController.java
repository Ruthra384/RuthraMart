package com.ruthramart.RuthraMart.controller;

import com.ruthramart.RuthraMart.model.Review;
import com.ruthramart.RuthraMart.model.User;
import com.ruthramart.RuthraMart.repository.OrderItemRepository;
import com.ruthramart.RuthraMart.repository.ReviewRepository;
import com.ruthramart.RuthraMart.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;

    public ReviewController(ReviewRepository reviewRepository,
                            OrderItemRepository orderItemRepository,
                            AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.authService = authService;
    }

    @GetMapping("/products/{id}/reviews")
    public ResponseEntity<?> getProductReviews(@PathVariable Long id) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByReviewDateDesc(id);
        Double avg = reviewRepository.getAverageRatingForProduct(id);
        double roundedAvg = (avg != null && !avg.isNaN()) ? Math.round(avg * 10.0) / 10.0 : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);
        response.put("averageRating", roundedAvg);
        response.put("totalReviews", reviews.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Optional<User> userOpt = authService.getUserFromRequest(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please log in to write a review."));
        }
        User user = userOpt.get();

        Long productId;
        int rating;
        String comment = (String) body.get("comment");

        try {
            productId = Long.parseLong(String.valueOf(body.get("productId")));
            rating = Integer.parseInt(String.valueOf(body.get("rating")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid product ID or rating."));
        }

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("message", "Rating must be between 1 and 5 stars."));
        }

        if (comment == null || comment.trim().length() < 3) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please provide a written review comment."));
        }

        Review review = new Review(
                productId,
                user.getId(),
                user.getName(),
                rating,
                comment.trim()
        );

        Review saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Review submitted successfully!",
                "review", saved
        ));
    }
}
