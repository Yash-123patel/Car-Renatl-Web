package com.tekworks.rental.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tekworks.rental.dto.UserDto;
import com.tekworks.rental.entity.Users;
import com.tekworks.rental.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {

    @Mock
    private UsersRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserLoginService loginService;

    @Test
    void RegisterUserTest() {
        UserDto dto = new UserDto();
        dto.setName("Yash Patel");
        dto.setCity("Vijayawad");
        dto.setEmail("patelyash@gmail.com");
        dto.setPassword("Yash@123");
        dto.setPhoneNo("9301498676");
        dto.setRole("ROLE_USER");
        dto.setLicenseNo("1234YAsh");

        Users user = convertToEntity(dto);
        user.setCreatedAt(Instant.now());

        when(repository.save(any(Users.class))).thenReturn(user);

        Users savedUser = loginService.register(dto);

        Assertions.assertAll(
            () -> Assertions.assertEquals("Yash Patel", savedUser.getName(), "Name is different"),
            () -> Assertions.assertEquals("Vijayawad", savedUser.getCity(), "City is different"),
            () -> Assertions.assertEquals("patelyash@gmail.com", savedUser.getEmail(), "Email is different"),
            () -> Assertions.assertEquals("9301498676", savedUser.getPhoneNo(), "Phone number is different"),
            () -> Assertions.assertEquals("ROLE_USER", savedUser.getRole(), "Role is different"),
            () -> Assertions.assertEquals("1234YAsh", savedUser.getLicenseNo(), "License number is different")
        );
    }

    private Users convertToEntity(UserDto userDto) {
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPhoneNo(userDto.getPhoneNo());
        user.setLicenseNo(userDto.getLicenseNo());
        user.setCity(userDto.getCity());
        user.setRole(userDto.getRole());
        return user;
    }
}
