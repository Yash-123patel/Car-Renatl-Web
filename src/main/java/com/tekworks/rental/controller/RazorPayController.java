package com.tekworks.rental.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.razorpay.Order;
import com.tekworks.rental.dto.CreateOrderDTO;
import com.tekworks.rental.dto.OrderResponseDTO;
import com.tekworks.rental.dto.VerifyPaymentDTO;
import com.tekworks.rental.entity.BookingPayment;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.exception.CustomException;
import com.tekworks.rental.repository.UsersRepository;
import com.tekworks.rental.service.RazorpayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment")
public class RazorPayController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private UsersRepository usersRepository;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        try {
            Users user = usersRepository.findById(createOrderDTO.getUserId())
                    .orElseThrow(() -> new CustomException("User not found with id: " + createOrderDTO.getUserId(), HttpStatus.NOT_FOUND));

            Order order = razorpayService.createOrder(createOrderDTO);
            BookingPayment bookingPayment = new BookingPayment();
            bookingPayment.setOrderId(order.get("id"));
            bookingPayment.setAmount(createOrderDTO.getAmount());
            bookingPayment.setUser(user);
            bookingPayment.setCreatedAt(Instant.now());
            bookingPayment.setCurrency("INR");
            bookingPayment.setOrderDate(Instant.now());
            bookingPayment.setIsOrderActive(true);
            bookingPayment.setPaymentStatus("created");

            razorpayService.savePaymentOrder(bookingPayment);

            return ResponseEntity.ok(new OrderResponseDTO("Order Created Successfully", order.get("id"), createOrderDTO.getUserId()));
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order");
        }
    }

    @PostMapping("/verifyPayment/{userId}")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody VerifyPaymentDTO verifyPaymentDTO, @PathVariable Long userId) {
        try {
            usersRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

            boolean isVerified = razorpayService.verifyPayment(verifyPaymentDTO);
            if (!isVerified) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
            }

            BookingPayment order = razorpayService.getCreatedOrderById(verifyPaymentDTO.getOrderId(), userId);
            String paymentStatus = razorpayService.getPaymentStatus(verifyPaymentDTO.getPaymentId());

            order.setPaymentStatus(paymentStatus);
            order.setUpdatedAt(Instant.now());
            razorpayService.savePaymentOrder(order);

            return ResponseEntity.ok("Payment Verified Successfully");
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment");
        }
    }

}
