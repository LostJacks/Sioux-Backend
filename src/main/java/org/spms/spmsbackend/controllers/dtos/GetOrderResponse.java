package org.spms.spmsbackend.controllers.dtos;

import java.util.ArrayList;
import java.util.List;

public class GetOrderResponse {
    public GetOrderResponse() {};

    private List<OrderDTO> orderList = new ArrayList();

    public List<OrderDTO> getAllOrders() {return this.orderList;}

    public void setOrderList(List<OrderDTO> orderList) {this.orderList = orderList;}
}
