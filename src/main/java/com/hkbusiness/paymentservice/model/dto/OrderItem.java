package com.hkbusiness.paymentservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productCode;
    private Integer productQuantity;
    private Double productPrice;
}
