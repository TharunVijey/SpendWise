
package com.tharun.spendwise.controller;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tharun.spendwise.dto.LoginRequest;
import com.tharun.spendwise.dto.LoginResponse;
import com.tharun.spendwise.dto.RegisterRequest;
import com.tharun.spendwise.dto.RegisterResponse;
import com.tharun.spendwise.entity.User;
import com.tharun.spendwise.repository.UserRepository;
import com.tharun.spendwise.service.AuthService;
import com.tharun.spendwise.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(
            AuthService authService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository) {

        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

        User user = userRepository.findByEmail(
                authentication.getName()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        ));

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token,
                "Bearer",
                3600
        );

        return ResponseEntity.ok(response);
    }
}
