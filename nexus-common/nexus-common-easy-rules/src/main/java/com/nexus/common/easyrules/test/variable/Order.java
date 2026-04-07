package com.nexus.common.easyrules.test.variable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderNo;

    private Integer price;

    private String message;
}
