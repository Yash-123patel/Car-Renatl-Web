package com.tekworks.rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekworks.rental.entity.BookingPayment;

@Repository
public interface BookingPaymentRepository extends JpaRepository<BookingPayment, String> {


    Optional<BookingPayment> findByOrderIdAndUser_IdAndPaymentStatus(String orderId, Long userId, String paymentStatus);
}

