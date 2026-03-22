package com.goviya.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseLoginRequest {
    @NotBlank
    private String idToken;
}
