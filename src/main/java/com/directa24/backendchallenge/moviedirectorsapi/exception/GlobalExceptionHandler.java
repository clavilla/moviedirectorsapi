package com.directa24.backendchallenge.moviedirectorsapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidThresholdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidThresholdException(InvalidThresholdException ex) {
        return ex.getMessage();
    }
}
