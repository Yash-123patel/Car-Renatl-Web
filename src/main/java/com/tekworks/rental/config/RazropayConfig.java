package com.tekworks.rental.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazropayConfig {


    @Value("${razorpay.key}")
    private String razorPayKey;
 
    @Value("${razorpay.secret}")
    private String razorPaySecret;
    
    
	@Bean
    RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(razorPayKey, razorPaySecret);
    }
}
