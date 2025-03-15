// src/main/java/com/example/userauthservice/service/UserService.java
package com.example.userauthservice.service;

import com.example.userauthservice.dto.UserProfileResponse;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get all users
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     *
     * @param id User ID
     * @return User
     */
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Update user profile
     *
     * @param userId User ID
     * @param profileRequest Profile data to update
     * @return Updated user
     */
    public User updateUserProfile(String userId, UserProfileResponse profileRequest) {
        User user = getUserById(userId);

        // Only update allowed fields
        if (profileRequest.getName() != null) {
            user.setName(profileRequest.getName());
        }
        if (profileRequest.getImageUrl() != null) {
            user.setImageUrl(profileRequest.getImageUrl());
        }

        return userRepository.save(user);
    }

    /**
     * Update user roles
     *
     * @param userId User ID
     * @param roles New roles
     * @return Updated user
     */
    public User updateUserRoles(String userId, List<User.Role> roles) {
        User user = getUserById(userId);

        // Update roles
        Set<User.Role> roleSet = new HashSet<>(roles);
        user.setRoles(roleSet);

        return userRepository.save(user);
    }

    /**
     * Delete user
     *
     * @param userId User ID
     */
    public void deleteUser(String userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}