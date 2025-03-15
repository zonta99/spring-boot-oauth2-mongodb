// src/main/java/com/example/userauthservice/dto/UserProfileResponse.java
package com.example.userauthservice.dto;

import com.example.userauthservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String imageUrl;
    private Boolean emailVerified;
    private Set<User.Role> roles;
    private User.AuthProvider provider;
}