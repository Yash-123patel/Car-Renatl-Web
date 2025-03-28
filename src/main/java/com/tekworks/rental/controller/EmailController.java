package com.tekworks.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekworks.rental.dto.EmailDTO;
import com.tekworks.rental.service.EmailService;



@RestController
@RequestMapping("/email")
public class EmailController {

	
	@Autowired
	private EmailService emailService;
	
	@PostMapping("/sendEmail")
	public String sendEmail(@RequestBody EmailDTO emailDTO) {
		try {
			emailService.sendEmail(emailDTO);
			return "Mail sent successfully";
		} catch (Exception e) {
			System.out.println("Internal error: "+ e.getMessage());
			return "Some internal Server error";
		}
	}
}
