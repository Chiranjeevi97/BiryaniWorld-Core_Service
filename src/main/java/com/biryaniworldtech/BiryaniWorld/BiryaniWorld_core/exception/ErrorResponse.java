package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String message;
    private String details;
} 