package com.hkbusiness.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String orderNumber;
    private List<OrderItem> orderItems;
}
