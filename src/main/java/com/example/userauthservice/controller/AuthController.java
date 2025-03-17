// src/main/java/com/example/userauthservice/controller/AuthController.java
package com.example.userauthservice.controller;

import com.example.userauthservice.dto.*;
import com.example.userauthservice.exception.BadRequestException;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.core.CurrentUser;
import com.example.userauthservice.security.core.UserPrincipal;
import com.example.userauthservice.security.jwt.TokenProvider;
import com.example.userauthservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;  // Add this field


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

    @PostMapping("/set-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> setPassword(
            @Valid @RequestBody SetPasswordRequest setPasswordRequest,
            @CurrentUser UserPrincipal userPrincipal) {

        try {
            // Verify passwords match
            if (!setPasswordRequest.getPassword().equals(setPasswordRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Passwords do not match"));
            }

            // Get the current user
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

            // Encode the new password
            String encodedPassword = passwordEncoder.encode(setPasswordRequest.getPassword());

            // Update the user's password and add LOCAL provider if not already present
            user.setPassword(encodedPassword);

            // For users who only have OAuth providers, add LOCAL provider option
            if (user.getProvider() != User.AuthProvider.LOCAL) {
                // If using the linked providers approach
                if (user.getLinkedProviders() != null) {
                    user.getLinkedProviders().put(User.AuthProvider.LOCAL, user.getId());
                }
            }

            // Save the updated user
            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponse(true, "Password set successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to set password: " + e.getMessage()));
        }
    }

}