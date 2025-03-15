// src/main/java/com/example/userauthservice/security/CustomUserDetailsService.java
package com.example.userauthservice.security.oauth2.user;

import com.example.userauthservice.exception.ResourceNotFoundException;
import com.example.userauthservice.model.User;
import com.example.userauthservice.repository.UserRepository;
import com.example.userauthservice.security.core.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom implementation of UserDetailsService to load user-specific data for authentication.
 * This service is used by the authentication manager to verify user credentials and
 * build a UserPrincipal for authenticated users.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
                );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "id", id)
                );

        return UserPrincipal.create(user);
    }
}