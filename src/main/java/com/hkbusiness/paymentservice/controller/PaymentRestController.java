package com.hkbusiness.paymentservice.controller;

import com.hkbusiness.paymentservice.model.PaymentEntity;
import com.hkbusiness.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentRestController {

    private final PaymentRepository paymentRepository;

    @GetMapping
    public List<PaymentEntity> payments(){
        return paymentRepository.findAll();
    }


}
