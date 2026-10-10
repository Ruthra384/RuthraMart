package com.ruthramart.RuthraMart.repository;

import com.ruthramart.RuthraMart.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByReviewDateDesc(Long productId);
    boolean existsByProductIdAndBuyerId(Long productId, Long buyerId);
    long countByProductId(Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.productId = :productId")
    Double getAverageRatingForProduct(@Param("productId") Long productId);
}
