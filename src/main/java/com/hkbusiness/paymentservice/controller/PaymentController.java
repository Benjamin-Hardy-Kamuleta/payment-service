package com.hkbusiness.paymentservice.controller;

import com.hkbusiness.paymentservice.model.PaymentEntity;
import com.hkbusiness.paymentservice.model.dto.Order;
import com.hkbusiness.paymentservice.service.PaymentService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;


    @GetMapping("/payment/order/{orderNumber}")
    public String home(@PathVariable String orderNumber, Model model){
        model.addAttribute("orderNumber", orderNumber);
        Optional<PaymentEntity> payment = paymentService.payments().stream().filter(pay -> pay.getOrderNumber().equals(orderNumber)).findFirst();
        if (payment.isPresent()){
            return "already_payed";
        }
        if (!paymentService.isOrderExist(orderNumber)){
            return "order_not_found";
        }

        Order order = paymentService.order(orderNumber);
        Double amount = paymentService.orderAmount(order);
        String description = "Payment for order number: "+orderNumber;
        model.addAttribute("amount",amount);
        model.addAttribute("description", description);

        return "index";
    }
    @PostMapping("/payment/create/{orderNumber}")
    public RedirectView createPayment(
            @PathVariable("orderNumber") String orderNumber,
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description
    ){
        try {
            String cancelUrl = "http://localhost:9106/payment/cancel";
            String successUrl = "http://localhost:9106/payment/success/"+orderNumber;
            Payment payment = paymentService.createPayment(
                    Double.valueOf(amount),currency,method,"sale", description,
                    cancelUrl,successUrl);

            for(Links links: payment.getLinks()){
                if (links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PayPalRESTException e){
            log.error("Error occurred::",e);
        }
        return new RedirectView("/payment/error/"+orderNumber);
    }
    @GetMapping("/payment/success/{orderNumber}")
    public  String paymentSuccess(
            @PathVariable("orderNumber") String orderNumber,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId){
        try {
            Payment payment = paymentService.executePayment(paymentId,payerId);
            if (payment.getState().equals("approved")){
                PaymentEntity paymentEntity = PaymentEntity.builder()
                        .id(payment.getId())
                        .payerFullName(
                                payment.getPayer().getPayerInfo().getFirstName()+" "+
                                payment.getPayer().getPayerInfo().getLastName()+" "+
                                payment.getPayer().getPayerInfo().getFirstName())
                        .orderNumber(orderNumber)
                        .amountPayed(Double.valueOf(payment.getTransactions().get(0).getAmount().getTotal()))
                        .paymentStatus(payment.getState())
                        .createdOn(LocalDateTime.now())
                        .build();
                paymentService.savePayment(paymentEntity);
                return "paymentSuccess";
            }
        }catch (PayPalRESTException e){
            log.error("Error occurred::",e);
        }
        return "paymentSuccess";
    }
    @GetMapping("/payment/cancel")
    public  String paymentCancel(){
        return "paymentCancel";
    }
    @GetMapping("/payment/error/{orderNumber}")
    public  String paymentError(@PathVariable String orderNumber, Model model){
        model.addAttribute("orderNumber", orderNumber);
        return "paymentError";
    }


}
