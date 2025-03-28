package com.tekworks.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.tekworks.rental.dto.CreateOrderDTO;
import com.tekworks.rental.dto.OrderResponseDTO;
import com.tekworks.rental.exception.CustomException;
import com.tekworks.rental.service.RazorpayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment")
public class RazorPayController {
	
	@Autowired
	private RazorpayService razorpayService;
	
	@PostMapping("/createOrder")
	public ResponseEntity<?> createOrder( @Valid  @RequestBody CreateOrderDTO createOrderDTO){
		try {
			Order order = razorpayService.createOrder(createOrderDTO);
			String orderId = order.get("id");
			
			OrderResponseDTO responseDTO=new OrderResponseDTO();
			responseDTO.setMessage("Order Created Successfully");
			responseDTO.setOrderId(orderId);
			responseDTO.setUserId(createOrderDTO.getUserId());
			return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			
		}catch (CustomException e) {
			return ResponseEntity.status(e.getStatus()).body(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Error While creating order: "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error while creating order");
		}
	}

}
