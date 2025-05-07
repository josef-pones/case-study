package com.rohlikgroup.casestudy.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rohlikgroup.casestudy.entity.Order;
import com.rohlikgroup.casestudy.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderItemsProductIdAndStatusNotIn(Long productId, Set<OrderStatus> statuses);

    @Query("select o from Order o where o.createdAt<= :createdAt and o.status='PENDING'")
    List<Order> findUnpaidOrders(@Param(value = "createdAt") LocalDateTime createdAt);
}
