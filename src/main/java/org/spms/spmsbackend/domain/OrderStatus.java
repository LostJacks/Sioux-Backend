package org.spms.spmsbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatus {
    private int id;
    private String name;      // e.g. "REGISTERED", "READY_FOR_PICKUP"
    private String description;
}
