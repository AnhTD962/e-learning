package com.domain.backend.security;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    public Key jwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}