package com.reviewyme.paymentservice.repository;

import com.reviewyme.paymentservice.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByUniqueTransactionId(String uniqueTransactionId);

    Optional<List<Payment>> findByCustomerId(String customerId);

}
