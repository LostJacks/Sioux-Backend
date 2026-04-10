package org.spms.spmsbackend.controllers;

import org.spms.spmsbackend.dtos.GetOrderResponse;
import org.spms.spmsbackend.controllers.mappers.OrderMapper;
import org.spms.spmsbackend.domain.orders.Order;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public GetOrderResponse getOrders() {
        List<Order> orderList = orderService.getAllOrders();

        return OrderMapper.mapOrderToGetOrderResponse(orderList);
    }
}
