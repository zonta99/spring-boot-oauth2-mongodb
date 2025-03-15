// src/main/java/com/example/userauthservice/controller/AdminController.java
package com.example.userauthservice.controller;

import com.example.userauthservice.dto.ApiResponse;
import com.example.userauthservice.dto.UserProfileResponse;
import com.example.userauthservice.model.User;
import com.example.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserProfileResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> UserProfileResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .imageUrl(user.getImageUrl())
                        .emailVerified(user.getEmailVerified())
                        .roles(user.getRoles())
                        .provider(user.getProvider())
                        .build())
                .collect(Collectors.toList());
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable String userId,
                                             @RequestBody List<User.Role> roles) {
        userService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(new ApiResponse(true, "User roles updated successfully"));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }
}