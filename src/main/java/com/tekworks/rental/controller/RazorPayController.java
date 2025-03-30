package com.tekworks.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tekworks.rental.dto.CreateOrderDTO;
import com.tekworks.rental.dto.OrderResponseDTO;
import com.tekworks.rental.dto.VerifyPaymentDTO;
import com.tekworks.rental.exception.CustomException;
import com.tekworks.rental.service.RazorpayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment")
public class RazorPayController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        try {
            OrderResponseDTO response = razorpayService.createOrder(createOrderDTO);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error While creating order");
        }
    }

    @PostMapping("/verifyPayment/{userId}")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody VerifyPaymentDTO verifyPaymentDTO, @PathVariable Long userId) {
        try {
            
            boolean isVerified = razorpayService.verifyPayment(verifyPaymentDTO, userId);
            
            if (isVerified) {
                return ResponseEntity.ok("Payment verified successfully.");
            } 
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed. Please check Signature and payment id");
        } catch (CustomException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error While verifying payment");
        }
    }
}
