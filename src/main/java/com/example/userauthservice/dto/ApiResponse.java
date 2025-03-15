// src/main/java/com/example/userauthservice/dto/ApiResponse.java
package com.example.userauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
}