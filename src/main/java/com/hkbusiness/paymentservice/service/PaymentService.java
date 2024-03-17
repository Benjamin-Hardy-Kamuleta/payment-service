package com.hkbusiness.paymentservice.service;

import com.hkbusiness.paymentservice.model.PaymentEntity;
import com.hkbusiness.paymentservice.model.dto.Order;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.util.List;

public interface PaymentService {
    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;
    Double orderAmount(Order order);
    Order order(String orderNumber);
    boolean isOrderExist(String orderNumber);
    List<PaymentEntity> payments();
    PaymentEntity savePayment(PaymentEntity paymentEntity);
    Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException;

}
