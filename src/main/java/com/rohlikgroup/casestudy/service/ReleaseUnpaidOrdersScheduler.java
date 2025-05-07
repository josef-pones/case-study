package com.rohlikgroup.casestudy.service;


import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rohlikgroup.casestudy.entity.OrderStatus;
import com.rohlikgroup.casestudy.repository.OrderRepository;
import com.rohlikgroup.casestudy.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReleaseUnpaidOrdersScheduler {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void releaseUnpaidOrders() {
        orderRepository.findUnpaidOrders(LocalDateTime.now().minusMinutes(30)).forEach(order -> {
            order.getOrderItems().forEach(orderItem -> {
                var product = orderItem.getProduct();
                product.setStockAmount(product.getStockAmount() + orderItem.getQuantity());
                productRepository.save(product);
            });
            order.setStatus(OrderStatus.CANCELED);
        });
    }

}
