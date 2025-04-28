package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
} 