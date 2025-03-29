package com.tekworks.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPaymentDTO {
	
	@NotBlank(message = "Order id is required to verify payment")
	private String orderId;
	
	@NotBlank(message = "Payment id is required to verify payment")
	private String paymentId;
	
	@NotBlank(message = "signature is required to verify payment")
	private String signature;

}
