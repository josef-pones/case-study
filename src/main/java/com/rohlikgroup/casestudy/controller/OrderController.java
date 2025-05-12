package com.rohlikgroup.casestudy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rohlikgroup.casestudy.dto.CreateOrderRequest;
import com.rohlikgroup.casestudy.dto.ErrorResponseDto;
import com.rohlikgroup.casestudy.dto.OrderDto;
import com.rohlikgroup.casestudy.exception.InsufficientStockException;
import com.rohlikgroup.casestudy.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id) {
        var canceledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(canceledOrder);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDto> payOrder(@PathVariable Long id) {
        var paidOrder = orderService.setOrderPaid(id);
        return ResponseEntity.ok(paidOrder);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<OrderDto> createOrder(@RequestParam CreateOrderRequest orderRequest) {
        OrderDto order = orderService.createOrder(orderRequest);
        return ResponseEntity.accepted().body(order);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientStockException(InsufficientStockException ex) {
        return ResponseEntity.ofNullable(new ErrorResponseDto(HttpStatus.NOT_FOUND, "INSUFFICIENT_STOCK", ex.getMessage()));
    }
}
