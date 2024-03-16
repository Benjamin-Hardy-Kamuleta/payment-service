package com.hkbusiness.paymentservice.config.feign;

import com.hkbusiness.paymentservice.model.dto.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
@FeignClient("MICROSERVICE-ORDER")
public interface OrderClient {
    @GetMapping("/api/orders")
    List<Order> orders();
}
