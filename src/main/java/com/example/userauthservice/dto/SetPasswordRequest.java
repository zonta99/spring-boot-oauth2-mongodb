// src/main/java/com/example/userauthservice/dto/SetPasswordRequest.java
package com.example.userauthservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPasswordRequest {
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Password confirmation cannot be blank")
    private String confirmPassword;
}
