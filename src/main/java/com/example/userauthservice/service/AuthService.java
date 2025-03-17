// src/main/java/com/example/userauthservice/service/AuthService.java
package com.example.userauthservice.service;

import com.example.userauthservice.exception.BadRequestException;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with encoded password
     *
     * @param user User to register
     * @return Registered user
     */
    public User registerUser(User user) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        return userRepository.save(user);
    }
    /**
     * Set password for a user (useful for OAuth users who want to add local authentication)
     *
     * @param userId User ID
     * @param password New password (unencoded)
     * @param confirmPassword Password confirmation (must match password)
     * @return Updated user
     */
    public User setUserPassword(String userId, String password, String confirmPassword) {
        // Verify passwords match
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Encode the password
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        // Ensure the user can login with email/password by adding LOCAL provider
        if (user.getProvider() != User.AuthProvider.LOCAL) {
            // If using the linked providers approach
            if (user.getLinkedProviders() != null) {
                user.getLinkedProviders().put(User.AuthProvider.LOCAL, user.getId());
            }
        }

        return userRepository.save(user);
    }
}