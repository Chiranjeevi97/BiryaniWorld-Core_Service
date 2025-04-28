package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({InvalidRequestException.class, NoDataFoundException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ResponseEntity<?> handleInvalidRequestException(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", String.valueOf(LocalDateTime.now()));
        errorBody.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorBody.put("error", "Bad_Request");
        errorBody.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errorBody);
    }

}
