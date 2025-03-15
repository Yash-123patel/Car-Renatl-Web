package com.tekworks.rental.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekworks.rental.config.TwilioConfiguration;
import com.tekworks.rental.dto.LoginDto;
import com.tekworks.rental.dto.LoginResponseDto;
import com.tekworks.rental.dto.UserDto;
import com.tekworks.rental.dto.VerifyOtpDto;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.repository.UsersRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
public class UserLoginService {

	
	@Autowired 
	private UsersRepository usersRepository;

	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private TwilioConfiguration twilioConfiguration;
	
	 private Map<String, OtpData> otpMap = new HashMap<>();
	
	 //method for login the users
	public LoginResponseDto login(LoginDto dto) throws Exception {
	    Users user = usersRepository.findByEmail(dto.getEmail());

	    if (user == null) {
	        System.out.println("User not found");
	        return null;
	    }


	    if (encoder.matches(dto.getPassword(), user.getPassword())) {
	        LoginResponseDto responseDto = new LoginResponseDto();

	        String token = jwtService.generateToken(user.getEmail(),user.getRole());

	        responseDto.setId(user.getId());
	        responseDto.setEmail(dto.getEmail());
	        responseDto.setName(user.getName());
	        responseDto.setJwtToken(token);
	        responseDto.setLoginTIme(Instant.now());
	        responseDto.setPhoneNo(user.getPhoneNo());

	        return responseDto;
	    }

	    throw new RuntimeException("Invalid Password Please check");
	}

	
	//method for register the users
	public void register(UserDto userDto) {
		Users user = convertToEntity(userDto);
		String encode = encoder.encode(user.getPassword());
		user.setPassword(encode);
		user.setCreatedAt(Instant.now());
		usersRepository.save(user);
	}
	
	
	
	//method for sending otp in mobile number
	public String sendOtpToPhone(String mobileNumber) {
		
		String otp=generateOtp();
		
		String message = "Dear User,\n"
	               + "Your OTP for Ride Together login is"
	               +otp
	               + ". This code is valid for 10 minutes.\n"
	               + "Please do not share it with anyone.\n\n"
	               + "If you did not request this OTP, ignore this message.\n\n"
	               + "Thank you,\n"
	               + "Ride Together Team";


		
		try {
			 Message sentMessage = Message.creator(new PhoneNumber(mobileNumber),  //to phone number
					new PhoneNumber(twilioConfiguration.getPhoneNumber()), //from phone number
					message).create();
			
			System.out.println("Message :  "+sentMessage.getAccountSid());
			OtpData otpData=new OtpData(otp);
			otpMap.put(mobileNumber, otpData);
			System.out.println("Otp Data: "+otpData);
			return "Otp Send Successfully";
		} catch (Exception e) {
			System.out.println("Error: "+e.getMessage());
			return "Error while sending Otp";
		}
	}
	
	
	public String verifyOtp(VerifyOtpDto dto) {
		OtpData otpData = otpMap.get(dto.getMobileNumber());
		if(otpData==null) {
			return "Invalid mobile Number";
		}
		
		if(otpData.isExpired()) {
			otpMap.remove(dto.getMobileNumber());
			return "Otp is Expired";
		}
		
		if(!otpData.getOtp().equals(dto.getOtp())) {
			return "invalid otp";
		}
		
		otpMap.remove(dto.getMobileNumber());
		return "Otp Verified Successfully";
	}
	
	
	//method generating otp
	private String generateOtp() {
	    int random = 100000 + (int) (Math.random() * 900000);
	    return String.valueOf(random);
	}

	
	//method for getting user by email
	public UserDto getUserByEmail(String email) {
		Users user = usersRepository.findByEmail(email);
		if(user==null) {
			throw new RuntimeException("User Not found with email : "+email);
		}
		return convertToDTO(user);
	}
	
	public Users getUserById(Long id) {
	    return usersRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	
	
	private Users convertToEntity(UserDto userDto) {
		Users user=new Users();
		user.setEmail(userDto.getEmail());
		user.setName(userDto.getName());
		user.setPassword(userDto.getPassword());
		user.setPhoneNo(userDto.getPhoneNo());
		user.setLicenseNo(userDto.getLicenseNo());
		user.setCity(userDto.getCity());
		user.setRole(userDto.getRole());
		return user;
	
	}
	//method converting Entity to Userdto
	private UserDto convertToDTO(Users user) {
		UserDto dto=new UserDto();
		dto.setEmail(user.getEmail());
		dto.setPhoneNo(user.getPhoneNo());
		dto.setName(user.getName());
		dto.setCity(user.getCity());
		dto.setLicenseNo(user.getLicenseNo());
		return dto;
	
	}

	
	//private class for storing otp and creating time to verify

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class OtpData{
		private String otp;
		private Instant creationTime;
		private Instant expiryTime;
		
		 public OtpData(String otp) {
		        this.otp = otp;
		        this.creationTime = Instant.now();
		        this.expiryTime = creationTime.plusSeconds(120); 
		    }
		 
		 public boolean isExpired() {
		        return Instant.now().isAfter(expiryTime);
		    }
	}
	
	
}
