package org.spms.spmsbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSize {
    private int id;
    private String name;      // e.g. "Small", "Medium", "Big"
    private String description;
}
