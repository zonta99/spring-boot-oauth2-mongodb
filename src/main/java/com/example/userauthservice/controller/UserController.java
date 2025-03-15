// src/main/java/com/example/userauthservice/controller/UserController.java
package com.example.userauthservice.controller;

import com.example.userauthservice.dto.UserProfileResponse;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.core.CurrentUser;
import com.example.userauthservice.security.core.UserPrincipal;
import com.example.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserProfileResponse getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles())
                .provider(user.getProvider())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserProfileResponse getUserById(@PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles())
                .provider(user.getProvider())
                .build();
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateProfile(@CurrentUser UserPrincipal userPrincipal,
                                           @RequestBody UserProfileResponse profileRequest) {
        userService.updateUserProfile(userPrincipal.getId(), profileRequest);
        return ResponseEntity.ok().build();
    }
}