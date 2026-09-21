package com.realestate.due_diligence_agent;

import com.realestate.due_diligence_agent.dto.LoginRequest;
import com.realestate.due_diligence_agent.dto.RegisterRequest;
import com.realestate.due_diligence_agent.entity.Role;
import com.realestate.due_diligence_agent.entity.User;
import com.realestate.due_diligence_agent.exception.InvalidCredentialsException;
import com.realestate.due_diligence_agent.exception.UserAlreadyExistsException;
import com.realestate.due_diligence_agent.repository.UserRepository;
import com.realestate.due_diligence_agent.security.JwtService;
import com.realestate.due_diligence_agent.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerRejectsExistingEmailWithDomainException() {
        RegisterRequest request = new RegisterRequest("Existing User", "existing@example.com",
                "password123", Role.BUYER);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUsesPasswordEncoderAndReturnsToken() {
        LoginRequest request = new LoginRequest("buyer@example.com", "password123");
        User user = User.builder().id(1L).fullName("Buyer").email(request.getEmail())
                .password("stored-hash").role(Role.BUYER).build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        assertEquals("jwt-token", authService.login(request).getToken());
        verify(passwordEncoder).matches(request.getPassword(), "stored-hash");
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = new LoginRequest("buyer@example.com", "wrong");
        User user = User.builder().email(request.getEmail()).password("stored-hash")
                .role(Role.BUYER).build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verifyNoInteractions(jwtService);
    }
}
