// src/main/java/com/example/userauthservice/exception/OAuth2AuthenticationProcessingException.java
package com.example.userauthservice.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown during OAuth2 authentication processing when there's an issue
 * with the authentication flow such as missing email, provider mismatch, or other
 * validation failures.
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {

    /**
     * Constructs a new OAuth2AuthenticationProcessingException with the provided message.
     *
     * @param msg the detail message
     */
    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new OAuth2AuthenticationProcessingException with the provided message and cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
        super(msg, t);
    }
}