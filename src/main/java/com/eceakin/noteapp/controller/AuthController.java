package com.eceakin.noteapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eceakin.noteapp.application.dto.auth.AuthenticationRequest;
import com.eceakin.noteapp.application.dto.auth.AuthenticationResponse;
import com.eceakin.noteapp.application.dto.auth.RegisterRequest;
import com.eceakin.noteapp.application.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
	 private final AuthenticationService authenticationService;
	    
	    @PostMapping("/login")
	    public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest loginRequest) {
	        AuthenticationResponse response = authenticationService.authenticate(loginRequest);
	        return ResponseEntity.ok(response);
	    }
	    
	    @PostMapping("/register")
	    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
	        AuthenticationResponse response = authenticationService.register(signUpRequest);
	        return ResponseEntity.ok(response);
	    }
}