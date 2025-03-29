package com.tekworks.rental.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tekworks.rental.dto.CreateOrderDTO;
import com.tekworks.rental.dto.OrderResponseDTO;
import com.tekworks.rental.dto.VerifyPaymentDTO;
import com.tekworks.rental.entity.BookingPayment;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.exception.CustomException;
import com.tekworks.rental.repository.BookingPaymentRepository;
import com.tekworks.rental.repository.UsersRepository;

@Service
public class RazorpayService {

    @Value("${razorpay.secret}")
    private String razorPaySecret;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private BookingPaymentRepository bookingPaymentRepository;

    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) throws RazorpayException {
    	
            Users user = usersRepository.findById(createOrderDTO.getUserId())
                    .orElseThrow(() -> new CustomException("User Not found with id: " + createOrderDTO.getUserId(), HttpStatus.NOT_FOUND));

            boolean activeOrder = bookingPaymentRepository.existsByUser_IdAndIsOrderActive(user.getId(), true);
          
            if (activeOrder) {
                throw new CustomException("User already have an active order", HttpStatus.BAD_REQUEST);
            }

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", createOrderDTO.getAmount() * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("payment_capture", 1);

            Map<String, String> notes = new HashMap<>();
            notes.put("username", user.getName());
            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);

            BookingPayment bookingPayment = new BookingPayment();
            bookingPayment.setOrderId(order.get("id"));
            bookingPayment.setAmount(createOrderDTO.getAmount());
            bookingPayment.setUser(user);
            bookingPayment.setCreatedAt(Instant.now());
            bookingPayment.setCurrency("INR");
            bookingPayment.setOrderDate(Instant.now());
            bookingPayment.setIsOrderActive(false);
            bookingPayment.setPaymentStatus("created");

            bookingPaymentRepository.save(bookingPayment);

            return new OrderResponseDTO("Order Created Successfully", order.get("id"), createOrderDTO.getUserId());
        }

    public boolean verifyPayment(VerifyPaymentDTO verifyPaymentDTO, Long userId) throws Exception {
        
            usersRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

            BookingPayment order = bookingPaymentRepository.findByOrderIdAndUser_IdAndPaymentStatus(
                    verifyPaymentDTO.getOrderId(), userId, "created")
                    .orElseThrow(() -> new CustomException("No created order found for user", HttpStatus.NOT_FOUND));

            String payload = verifyPaymentDTO.getOrderId() + '|' + verifyPaymentDTO.getPaymentId();
            boolean isVerified = verifySignature(payload, verifyPaymentDTO.getSignature(), razorPaySecret);
           
            if (!isVerified) {
                return false;
            }

            String paymentStatus = razorpayClient.payments.fetch(verifyPaymentDTO.getPaymentId()).get("status");

            order.setPaymentStatus(paymentStatus);
            order.setUpdatedAt(Instant.now());
            if ("captured".equals(paymentStatus)) {
                order.setIsOrderActive(true);
            }
            bookingPaymentRepository.save(order);

            return true;
         
    }

    public static boolean verifySignature(String payload, String expectedSignature, String secret) throws Exception {
        String actualSignature = calculateRFC2104HMAC(payload, secret);
        return actualSignature.equals(expectedSignature);
    }

    private static String calculateRFC2104HMAC(String data, String secret) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encodeHex(rawHmac));
    }
}
