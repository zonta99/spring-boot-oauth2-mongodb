// src/main/java/com/example/userauthservice/service/UserService.java
package com.example.userauthservice.service;

import com.example.userauthservice.dto.UserProfileResponse;
import com.example.userauthservice.exception.BadRequestException;
import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.oauth2.user.OAuth2UserInfo;
import com.example.userauthservice.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public boolean linkUserAccount(String userId, User.AuthProvider provider, Map<String, Object> attributes) {
        User user = getUserById(userId);

        // Create OAuth2UserInfo to extract data
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                provider.toString().toLowerCase(), attributes);

        // Check if another account already uses this provider identity
        if (userRepository.findByProviderAndProviderId(provider, userInfo.getId()).isPresent()) {
            throw new BadRequestException("This " + provider + " account is already linked with another user");
        }

        // Link the provider
        user.linkProvider(provider, userInfo.getId());

        // Update user's profile info if needed
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(userInfo.getName());
        }
        if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
            user.setImageUrl(userInfo.getImageUrl());
        }

        userRepository.save(user);
        return true;
    }
}