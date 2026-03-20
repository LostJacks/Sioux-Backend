package org.spms.spmsbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private User employee;
    private String trackingNumber;
    private OrderSize size;
    private boolean pickedUp;
    private LocalDateTime pickedUpAt;
    private OrderStatus status;
    private LocalDateTime deliveredAt;
}
