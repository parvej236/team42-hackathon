package com.team42.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentRecordEntity, Long> {
    Optional<PaymentRecordEntity> findByPaymentId(String paymentId);
    Optional<PaymentRecordEntity> findByBookingRef(String bookingRef);
    List<PaymentRecordEntity> findByUserId(String userId);
}
