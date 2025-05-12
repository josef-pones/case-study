package com.rohlikgroup.casestudy.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.rohlikgroup.casestudy.dto.CreateOrderRequest;
import com.rohlikgroup.casestudy.dto.OrderDto;
import com.rohlikgroup.casestudy.dto.OrderItemRequest;
import com.rohlikgroup.casestudy.entity.Order;
import com.rohlikgroup.casestudy.entity.Product;
import com.rohlikgroup.casestudy.repository.OrderRepository;
import com.rohlikgroup.casestudy.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Test
    void testCreateOrder_Success() {
        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(
            new OrderItemRequest(1L, 2)
        ));
        Product product = new Product();
        product.setId(1L);
        product.setStockAmount(10);
        product.setName("Test Product");

        Order order = new Order();

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        verify(productRepository).save(product);
        assertEquals(8, product.getStockAmount());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(
            new OrderItemRequest(1L, 2)
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderRequest));
    }

    @Test
    void testCreateOrder_NotEnoughStock() {
        CreateOrderRequest orderRequest = new CreateOrderRequest(List.of(
            new OrderItemRequest(1L, 20)
        ));
        Product product = new Product();
        product.setId(1L);
        product.setStockAmount(10);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> orderService.createOrder(orderRequest));
    }
}