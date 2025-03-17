// src/main/java/com/example/userauthservice/model/User.java
package com.example.userauthservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String name;
    private String imageUrl;

    @Builder.Default
    private Boolean emailVerified = false;

    private String password;

    private AuthProvider provider;

    private String providerId;

    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private Map<AuthProvider, String> linkedProviders = new HashMap<>();

    // For backward compatibility
    public AuthProvider getProvider() {
        // Return the primary provider (first one or LOCAL)
        return linkedProviders.isEmpty() ? AuthProvider.LOCAL :
                linkedProviders.keySet().iterator().next();
    }

    public String getProviderId() {
        return linkedProviders.get(getProvider());
    }

    // Helper methods
    public void linkProvider(AuthProvider provider, String providerId) {
        this.linkedProviders.put(provider, providerId);
    }

    public boolean hasProvider(AuthProvider provider) {
        return this.linkedProviders.containsKey(provider);
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, GITHUB
    }

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }
}