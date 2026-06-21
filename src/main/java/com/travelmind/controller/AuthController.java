package com.travelmind.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Issues a short-lived JWT for valid credentials. Demo credentials come from
 * config (security.demo.*). In production this would verify against a user store.
 */
@RestController
public class AuthController {

    private final JwtEncoder jwtEncoder;

    @Value("${security.demo.username}")
    private String demoUsername;

    @Value("${security.demo.password}")
    private String demoPassword;

    public AuthController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/token")
    public String token(@RequestBody LoginRequest loginRequest) {
        if (!demoUsername.equals(loginRequest.username()) || !demoPassword.equals(loginRequest.password())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("travelmind")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(loginRequest.username())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public record LoginRequest(String username, String password) {
    }
}
