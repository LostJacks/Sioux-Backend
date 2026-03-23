package org.spms.spmsbackend.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.spms.spmsbackend.domain.Order;
import org.spms.spmsbackend.exceptions.NotFoundException;
import org.spms.spmsbackend.logic.interfaces.IOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private IOrderService IOrderService;

    @GetMapping
    public List<Order> getAllOrders(){ return this.IOrderService.getAllOrders();}

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable int id, HttpServletResponse response){
        try{
            return this.IOrderService.getOrderById(id);
        } catch (NotFoundException ex){
            response.setStatus(418);
            return null;
        }
    }
}
