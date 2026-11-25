
package com.tharun.spendwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

import jakarta.servlet.DispatcherType;
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // JWT authentication is stateless, so CSRF protection
            // is not required for this Bearer-token API.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})

            // Do not create or use HTTP sessions.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Only registration and login are publicly accessible.
            .authorizeHttpRequests(auth -> auth
    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
    .requestMatchers(
        "/api/auth/register",
        "/api/auth/login"
    ).permitAll()
    .anyRequest().authenticated()
)

            // Configure HTTP security headers.
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            )

            // Handle missing or invalid authentication.
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setHeader(
                        HttpHeaders.CACHE_CONTROL,
                        "no-store"
                    );
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                        "{\"error\":\"Unauthorized\",\"message\":\"Authentication is required or the token is invalid.\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setHeader(
                        HttpHeaders.CACHE_CONTROL,
                        "no-store"
                    );
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                        "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource.\"}"
                    );
                })
            )

            // Validate JWT Bearer tokens.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> {}
            ));

        return http.build();
    }
}
