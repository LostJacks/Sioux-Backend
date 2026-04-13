package org.spms.spmsbackend.mapper;

import org.spms.spmsbackend.dtos.GetOrderResponse;
import org.spms.spmsbackend.dtos.OrderDTO;
import org.spms.spmsbackend.domain.orders.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static GetOrderResponse mapOrderToGetOrderResponse(List<Order> input) {
        List<OrderDTO> orderList = new ArrayList<>();
        for (Order o : input) {
            orderList.add(orderToOrderDTO(o));
        }

        GetOrderResponse response = new GetOrderResponse();
        response.setOrderList(orderList);

        return response;
    }

    public static OrderDTO orderToOrderDTO(Order input) {
        OrderDTO output = new OrderDTO();

        output.setId(input.getId());
        output.setEmployee(input.getEmployee());
        output.setTrackingNumber(String.valueOf(input.getTrackingNumber()));
        output.setSize(input.getSize());
        output.setPickedUp(input.isPickedUp());
        output.setPickedUpAt(input.getPickedUpAt());
        output.setStatus(input.getStatus());
        output.setDeliveredAt(input.getDeliveredAt());

        return output;
    }
}
