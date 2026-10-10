package com.ruthramart.RuthraMart.repository;

import com.ruthramart.RuthraMart.model.Order;
import com.ruthramart.RuthraMart.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findBySellerId(Long sellerId);
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT DISTINCT oi.order FROM OrderItem oi WHERE oi.sellerId = :sellerId ORDER BY oi.order.createdAt DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(oi.subtotal), 0.0) FROM OrderItem oi WHERE oi.sellerId = :sellerId AND oi.order.status <> 'Cancelled'")
    Double calculateSellerSales(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.sellerId = :sellerId")
    long countDistinctOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END FROM OrderItem oi WHERE oi.order.buyerId = :buyerId AND oi.productId = :productId AND oi.order.status <> 'Cancelled'")
    boolean hasBuyerPurchasedProduct(@Param("buyerId") Long buyerId, @Param("productId") Long productId);
}
