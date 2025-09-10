package com.eceakin.noteapp.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.info("=== JWT Filter Processing: {} {}", request.getMethod(), requestURI);
        log.info("=== Authorization header: {}", request.getHeader("Authorization"));
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("=== Skipping OPTIONS request");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip auth for public endpoints
        if (requestURI.startsWith("/api/auth/") || requestURI.startsWith("/api/debug/")) {
            log.info("=== Skipping auth for public endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        log.info("=== Processing authentication for protected endpoint: {}", requestURI);
        
        try {
            String jwt = parseJwt(request);
            
            if (jwt != null) {
                log.info("=== JWT token found, length: {}", jwt.length());
                String username = jwtUtils.extractUsername(jwt);
                log.info("=== Username from JWT: {}", username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.info("=== User details loaded: {}", userDetails.getUsername());
                    
                    if (jwtUtils.validateToken(jwt, userDetails)) {
                        log.info("=== JWT token is VALID - Setting authentication");
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("=== Authentication SET for user: {}", username);
                    } else {
                        log.error("=== JWT token validation FAILED for user: {}", username);
                    }
                }
            } else {
                log.error("=== NO JWT TOKEN found for request: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("=== Authentication ERROR for {}: {}", requestURI, e.getMessage(), e);
        }
        
        // Check what's in SecurityContext before continuing
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("=== Final SecurityContext authentication: {}", 
                 auth != null ? auth.getClass().getSimpleName() + " - " + auth.getName() : "NULL");
        
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        log.debug("Authorization header: {}", headerAuth != null ? "Present (length: " + headerAuth.length() + ")" : "Not found");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            log.debug("Extracted token length: {}, starts with: {}", 
                     token.length(), 
                     token.length() > 10 ? token.substring(0, 10) : token);
            return token;
        }
        
        return null;
    }
}