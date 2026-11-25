
package com.tharun.spendwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Email is required")
@Email(message = "Enter a valid email")
@Pattern(
    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$",
    message = "Email must contain a valid domain with a dot"
)
@Size(max = 150, message = "Email cannot exceed 150 characters")
private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password cannot exceed 72 characters")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
