
package com.tharun.spendwise.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSecretKey() {

        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "JWT secret must be a valid Base64 value.",
                    exception
            );
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes."
            );
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {

        return NimbusJwtEncoder
                .withSecretKey(getSecretKey())
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // Validate issuer, expiration and other standard JWT timestamps.
        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer("spendwise")
        );

        return decoder;
    }
}
