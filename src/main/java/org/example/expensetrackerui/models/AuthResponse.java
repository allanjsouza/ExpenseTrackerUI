package org.example.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String message;
}
