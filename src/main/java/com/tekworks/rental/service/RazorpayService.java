package com.tekworks.rental.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tekworks.rental.dto.CreateOrderDTO;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.exception.CustomException;
import com.tekworks.rental.repository.UsersRepository;

@Service
public class RazorpayService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private RazorpayClient razorpayClient;

	public Order createOrder(CreateOrderDTO createOrderDTO) throws RazorpayException {
		Users user = usersRepository.findById(createOrderDTO.getUserId())
				.orElseThrow(() -> new CustomException("User Not found with id: "+createOrderDTO.getUserId(),HttpStatus.NOT_FOUND));

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", createOrderDTO.getAmount() * 100);
		orderRequest.put("currency", "INR");
		orderRequest.put("payment_capture", 1);

		Map<String, String> notes = new HashMap<>();
		notes.put("username", user.getName());

		orderRequest.put("notes", notes);

		Order order = razorpayClient.orders.create(orderRequest);

		if (order == null) {
			throw new RuntimeException("Order not created due to some problem");
		}
		return order;
	}

}
