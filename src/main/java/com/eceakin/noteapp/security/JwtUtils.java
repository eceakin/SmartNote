package com.eceakin.noteapp.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
    
    @Value("${noteapp.jwt.secret:mySecretKey}")
    private String jwtSecret;
    
    @Value("${noteapp.jwt.expiration:86400000}") // 24 hours
    private long jwtExpirationMs;
    
    @PostConstruct
    public void init() {
        log.info("=== JWT Secret length: {} characters", jwtSecret.length());
        if (jwtSecret.length() < 32) {
            log.error("=== JWT SECRET IS TOO SHORT! Must be at least 32 characters. Current: {}", jwtSecret.length());
        }
    } 
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername());
        log.info("=== Generated token for user: {}, token length: {}", userDetails.getUsername(), token.length());
        return token;
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            log.info("=== Validating token for user: {}", userDetails.getUsername());
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            log.info("=== Token validation result: {}", isValid);
            return isValid;
        } catch (MalformedJwtException e) {
            log.error("=== Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("=== JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("=== JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("=== JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("=== Unexpected JWT validation error: {}", e.getMessage(), e);
        }
        return false;
    }
    
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignKey())
                .compact();
    }
    
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}