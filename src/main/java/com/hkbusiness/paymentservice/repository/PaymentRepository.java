package com.hkbusiness.paymentservice.repository;

import com.hkbusiness.paymentservice.model.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<PaymentEntity, String> {
}
