package com.eceakin.noteapp.application.service;

import com.eceakin.noteapp.application.dto.auth.AuthenticationRequest;
import com.eceakin.noteapp.application.dto.auth.AuthenticationResponse;
import com.eceakin.noteapp.application.dto.auth.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}