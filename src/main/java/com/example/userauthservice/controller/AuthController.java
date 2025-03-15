// src/main/java/com/example/userauthservice/controller/AuthController.java
package com.example.userauthservice.controller;

import com.example.userauthservice.dto.ApiResponse;
import com.example.userauthservice.dto.AuthResponse;
import com.example.userauthservice.dto.LoginRequest;
import com.example.userauthservice.dto.SignupRequest;
import com.example.userauthservice.exception.BadRequestException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.jwt.TokenProvider;
import com.example.userauthservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword()) // Will be encoded in the service
                .provider(User.AuthProvider.LOCAL)
                .roles(Set.of(User.Role.ROLE_USER))
                .emailVerified(false)
                .build();

        User result = authService.registerUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }
}