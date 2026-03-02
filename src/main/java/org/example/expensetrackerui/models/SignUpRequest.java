package org.example.expensetrackerui.models;

import lombok.Data;

@Data
public class SignUpRequest {
    private String fullName;
    private String username;
    private String password;
}
