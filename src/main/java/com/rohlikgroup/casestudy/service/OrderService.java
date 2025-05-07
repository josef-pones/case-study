package com.rohlikgroup.casestudy.service;

import org.springframework.stereotype.Service;

import com.rohlikgroup.casestudy.dto.CreateOrderRequest;
import com.rohlikgroup.casestudy.dto.OrderDto;

@Service
public interface OrderService {

    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @return the created order
     */
    OrderDto createOrder(CreateOrderRequest order);

    /**
     * Cancels an order.
     *
     * @param orderId the ID of the order to cancel
     * @return the canceled order
     */
    OrderDto cancelOrder(Long orderId);

    /**
     * Pays an order.
     *
     * @param orderId the ID of the order to pay
     * @return the paid order
     */
    OrderDto setOrderPaid(Long orderId);

}
