
package com.tharun.spendwise.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle invalid request fields.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        response.put("error", "Bad Request");
        response.put("message", "Please check your input.");
        response.put("details", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Handle invalid JSON or unreadable request bodies.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(
            HttpMessageNotReadableException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Request body is missing or contains invalid data."
        );
    }

    // Handle application-defined HTTP errors.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatusException(
            ResponseStatusException exception) {

        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        String message = exception.getReason() != null
                ? exception.getReason()
                : "The request could not be completed.";

        return buildResponse(
                status,
                status.getReasonPhrase(),
                message
        );
    }

    // Handle unexpected errors without exposing internal details.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong. Please try again later."
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String error,
            String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("error", error);
        response.put("message", message);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
