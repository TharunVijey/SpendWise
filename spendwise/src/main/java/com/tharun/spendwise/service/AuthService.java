
package com.tharun.spendwise.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.dto.RegisterRequest;
import com.tharun.spendwise.dto.RegisterResponse;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        String name = request.getName().trim();

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String password = request.getPassword();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "All fields are required"
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password cannot exceed 72 bytes"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Store only the BCrypt-hashed password.
        user.setPassword(passwordEncoder.encode(password));

        try {
            User savedUser = userRepository.save(user);

            return new RegisterResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getCreatedAt(),
                    "Registration successful"
            );

        } catch (DataIntegrityViolationException exception) {
            // Handles duplicate email registration races.
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }
    }
}
