// src/main/java/com/example/userauthservice/security/jwt/TokenProvider.java
package com.example.userauthservice.security.jwt;

import com.example.userauthservice.security.core.CustomUserDetailsService;
import com.example.userauthservice.security.core.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenProvider {
    private final SecretKey key;
    private final long tokenExpirationMsec;
    private final UserDetailsService userDetailsService;

    public TokenProvider(
            @Value("${app.auth.tokenSecret:cff8c43509cf688e99090dbebd3aae0ed06a6baf65fa1438e3fbb3326dc59e9d}") String tokenSecret,
            @Value("${app.auth.tokenExpirationMsec:864000000}") long tokenExpirationMsec,
            UserDetailsService userDetailsService) {
        // Add fallback in case the property resolution still fails
        if (tokenSecret == null || tokenSecret.trim().isEmpty()) {
            tokenSecret = "cff8c43509cf688e99090dbebd3aae0ed06a6baf65fa1438e3fbb3326dc59e9d";
        }
        this.key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
        this.tokenExpirationMsec = tokenExpirationMsec;
        this.userDetailsService = userDetailsService;
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        return Jwts.builder()
                .setSubject(userPrincipal.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Creates a JWT token from just a user ID.
     * Preserved for future use in token refresh functionality.
     */
    @SuppressWarnings("unused")
    public String createToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        String userId = getUserIdFromToken(token);

        // Cast to CustomUserDetailsService to access the loadUserById method
        CustomUserDetailsService customUserDetailsService = (CustomUserDetailsService) userDetailsService;

        // Use loadUserById instead of loadUserByUsername
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}