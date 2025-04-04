package com.tekworks.rental.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekworks.rental.dto.LoginDto;
import com.tekworks.rental.dto.LoginResponseDto;
import com.tekworks.rental.dto.SendOtpDto;
import com.tekworks.rental.dto.UserDto;
import com.tekworks.rental.dto.UserProfilUpdateDto;
import com.tekworks.rental.dto.VerifyOtpDto;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.repository.UsersRepository;
import com.tekworks.rental.response.ErrorResponse;
import com.tekworks.rental.response.SuccessResponse;
import com.tekworks.rental.service.UserLoginService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class LoginController {

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private UsersRepository repository;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
	   
		try {
			    if (repository.existsByEmail(userDto.getEmail())) {
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Email Already Registered", Instant.now()));
			    }

			    if (repository.existsByPhoneNo(userDto.getPhoneNo())) {
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Phone Number Already Registered", Instant.now()));
			    }

			    Users user = userLoginService.register(userDto);
			   if(user==null) {
				 throw new Exception("User Can not registerd due to some error");
			   }
			   
			   return ResponseEntity.status(HttpStatus.OK)
			            .body( "Registered Successfully");
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	}


	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginDto dto ) {

		try {
			LoginResponseDto login = userLoginService.login(dto);

			if (login == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new ErrorResponse(HttpStatus.NOT_FOUND, "No user found with credentials", Instant.now()));
			}

			return ResponseEntity.status(HttpStatus.OK)
					.body(new SuccessResponse(HttpStatus.OK, "Login Successfully", login));
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), Instant.now()));
		} 
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	}
	
	@PostMapping("/sendOtp")
	public ResponseEntity<?> sendOtpToNumber(@Valid @RequestBody SendOtpDto sendOtpDto) {
	    
	    try {
	        String sendOtpToPhone = userLoginService.sendOtpToPhone(sendOtpDto.getMobileNumber());
	        if ("Otp Send Successfully".equals(sendOtpToPhone)) {
	            return ResponseEntity.status(HttpStatus.OK).body(sendOtpToPhone);
	        }
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sendOtpToPhone);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
	    }
	}
	
	@PostMapping("/verifyOtp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpDto verifyOtpDto){
		 try {
		        String verifyOtp = userLoginService.verifyOtp(verifyOtpDto);
		        if ("Otp Verified Successfully".equals(verifyOtp)) {
		            return ResponseEntity.status(HttpStatus.OK).body(verifyOtp);
		        }
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(verifyOtp);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		    }
	}

	
	@GetMapping("/viewProfileByEmail/{email}")
	public ResponseEntity<?> getUserDetailByEmail(@PathVariable String email){
		try {
			
			UserDto user = userLoginService.getUserByEmail(email);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new SuccessResponse(HttpStatus.OK, "User Detail", user));
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	
	}
	

	@GetMapping("/viewProfileById/{id}")
	public ResponseEntity<?> getUserDetailsById(@PathVariable Long id){
		try {
			
			UserDto user = userLoginService.getUserById(id);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new SuccessResponse(HttpStatus.OK, "User Detail", user));
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	
	}
	
	@PutMapping("/updateProfile/{id}")
	public ResponseEntity<?> updateProfileById(@Valid @RequestBody  UserProfilUpdateDto userDto ,@PathVariable Long id){
		
		try {
		    userLoginService.updateProfile(userDto,id);
		    return ResponseEntity.status(HttpStatus.OK)
		            .body( "Profile updated Successfully");
		    
	}catch (RuntimeException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
	}
	catch (Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
	}
	}

}
