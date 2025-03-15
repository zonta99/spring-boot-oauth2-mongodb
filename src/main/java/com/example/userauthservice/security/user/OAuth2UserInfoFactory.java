// src/main/java/com/example/userauthservice/security/user/OAuth2UserInfoFactory.java
package com.example.userauthservice.security.user;

import com.example.userauthservice.exception.OAuth2AuthenticationProcessingException;
import com.example.userauthservice.model.User.AuthProvider;
import com.example.userauthservice.security.oauth2.GithubOAuth2UserInfo;
import com.example.userauthservice.security.oauth2.GoogleOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}