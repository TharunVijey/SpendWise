
package com.tharun.spendwise.dto;

import java.time.LocalDateTime;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private String message;

    public RegisterResponse(
            Long id,
            String name,
            String email,
            LocalDateTime createdAt,
            String message) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }
}
