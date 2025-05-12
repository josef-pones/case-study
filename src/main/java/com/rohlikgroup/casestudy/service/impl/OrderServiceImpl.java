package com.rohlikgroup.casestudy.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rohlikgroup.casestudy.dto.CreateOrderRequest;
import com.rohlikgroup.casestudy.dto.OrderDto;
import com.rohlikgroup.casestudy.dto.OrderItemRequest;
import com.rohlikgroup.casestudy.entity.Order;
import com.rohlikgroup.casestudy.entity.OrderItem;
import com.rohlikgroup.casestudy.entity.OrderStatus;
import com.rohlikgroup.casestudy.exception.InsufficientStockException;
import com.rohlikgroup.casestudy.mapper.OrderMapper;
import com.rohlikgroup.casestudy.repository.OrderRepository;
import com.rohlikgroup.casestudy.repository.ProductRepository;
import com.rohlikgroup.casestudy.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderRequest orderRequest) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        orderRequest.orderItems().forEach(item -> {
            orderProduct(order, item);
        });
        orderRepository.save(order);
        return orderMapper.map(order);
    }

    private void orderProduct(Order order, OrderItemRequest item) {
        var product = productRepository
                .findById(item.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + item.productId()));

        if (product.getStockAmount() < item.quantity()) {
            throw new InsufficientStockException("Not enough stock for product: " + product.getName());
        }

        product.setStockAmount(product.getStockAmount() - item.quantity());
        productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(item.quantity());

        order.getOrderItems().add(orderItem);
        orderItem.setOrder(order);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel order in status " + order.getStatus());
        }

        for (OrderItem item : order.getOrderItems()) {
            productRepository.releaseStock(item.getProduct().getId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);

        return orderMapper.map(order);
    }

    @Override
    @Transactional
    public OrderDto setOrderPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be paid for");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        return orderMapper.map(order);
    }

}
