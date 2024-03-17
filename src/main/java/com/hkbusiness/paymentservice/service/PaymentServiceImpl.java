package com.hkbusiness.paymentservice.service;

import com.hkbusiness.paymentservice.config.feign.OrderClient;
import com.hkbusiness.paymentservice.model.PaymentEntity;
import com.hkbusiness.paymentservice.model.dto.Order;
import com.hkbusiness.paymentservice.model.dto.OrderItem;
import com.hkbusiness.paymentservice.repository.PaymentRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
   @Override
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency),"%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }
    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext,paymentExecution);

    }
    @Override
    public Order order(String orderNumber){
        return orderClient.orders().stream().filter(order -> order.getOrderNumber().equals(orderNumber))
                .findFirst().orElseThrow(()-> new RuntimeException("Order with orderNumber "+orderNumber+" not found"));
    }

    @Override
    public boolean isOrderExist(String orderNumber) {
      return orderClient.orders().stream().anyMatch(order -> order.getOrderNumber().equals(orderNumber));
    }

    @Override
    public List<PaymentEntity> payments() {
        return paymentRepository.findAll();
    }

    @Override
    public PaymentEntity savePayment(PaymentEntity paymentEntity) {
        return paymentRepository.save(paymentEntity);
    }

    public Double orderAmount(Order order){
        Double amount = 0.0;
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem item: orderItems){
            amount += item.getProductPrice();
        }
        return amount;
    }
}
