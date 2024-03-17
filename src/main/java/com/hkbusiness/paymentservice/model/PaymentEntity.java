package com.hkbusiness.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payments")
public class PaymentEntity {
    String id;
    String payerFullName;
    String orderNumber;
    Double amountPayed;
    String paymentStatus;
    LocalDateTime createdOn = LocalDateTime.now();

}
