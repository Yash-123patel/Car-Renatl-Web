package com.tekworks.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.tekworks.rental.dto.EmailDTO;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(EmailDTO emailDTO) {
  
       try {
    	   
    	  Integer otp=64448;
    	   String subject= "Your OTP for BitLabs Jobs Verification";
    	   String content = "Dear Applicant,\n\n" +
                   "Your OTP is: " + otp + "\n\n" +
                   "We received a request to verify your identity for bitLabs Jobs. To complete the sign-up process, please use the above One-Time Password (OTP).\n\n"
                   +
                   "This OTP is valid for the next 1 minute. For your security, please do not share this code with anyone.\n\n"
                   +
                   "If you did not request this verification, please ignore this email.\n\n" +
                   "Thank you for using bitLabs Jobs!\n\n" +
                   "Best regards,\n" +
                   "The bitLabs Jobs Team\n\n" +
                   "This is an auto-generated email. Please do not reply.";

    	   SimpleMailMessage message = new SimpleMailMessage();
    	   
    	   
    	   
           message.setFrom("postmaster@sandbox329e6466a8714d47a3cf0927f1e4800b.mailgun.org");
           message.setTo(emailDTO.getToEmail());
           message.setSubject(subject);
           message.setText(content);

           
           mailSender.send(message);
           System.out.println("Email sent successfully to " + emailDTO.getToEmail());
	} catch (Exception e) {
		System.out.println("Error : "+e.getMessage());
	}
    }
}
