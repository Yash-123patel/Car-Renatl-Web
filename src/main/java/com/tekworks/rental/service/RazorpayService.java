package com.tekworks.rental.service;

import java.nio.charset.StandardCharsets;
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

    public Order createOrder(CreateOrderDTO createOrderDTO) throws Exception {
        Users user = usersRepository.findById(createOrderDTO.getUserId())
                .orElseThrow(() -> new CustomException("User Not found with id: " + createOrderDTO.getUserId(), HttpStatus.NOT_FOUND));

        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", createOrderDTO.getAmount() * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);

        Map<String, String> notes = new HashMap<>();
        notes.put("username", user.getName());
        orderRequest.put("notes", notes);

        return razorpayClient.orders.create(orderRequest);
    }

    public boolean verifyPayment(VerifyPaymentDTO verifyPaymentDTO) {
        try {
            String payload = verifyPaymentDTO.getOrderId() +'|'+ verifyPaymentDTO.getPaymentId();
            return verifySignature(payload, verifyPaymentDTO.getSignature(),razorPaySecret);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPaymentStatus(String paymentId) throws RazorpayException {
        return razorpayClient.payments.fetch(paymentId).get("status");
    }

    public BookingPayment getCreatedOrderById(String orderId, Long userId) {
        return bookingPaymentRepository.findByOrderIdAndUser_IdAndPaymentStatus(orderId, userId, "created")
                .orElseThrow(() -> new CustomException("No created order found for user", HttpStatus.NOT_FOUND));
    }

    public void savePaymentOrder(BookingPayment bookingPayment) {
        bookingPaymentRepository.save(bookingPayment);
    }

    public static boolean verifySignature(String payload, String expectedSignature, String secret) throws Exception {
	    String actualSignature = calculateRFC2104HMAC(payload, secret);
	    System.out.println("Expected Signature: "+expectedSignature);
	    return actualSignature.equals(expectedSignature);
	  }	
 
     private static String calculateRFC2104HMAC(String data, String secret) throws Exception {
	    SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	    Mac mac = Mac.getInstance("HmacSHA256");
	    mac.init(signingKey);
	    byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	    System.out.println("Actual Signature is: "+new String(Hex.encodeHex(rawHmac)));
	    return new String(Hex.encodeHex(rawHmac));
	  }

}
