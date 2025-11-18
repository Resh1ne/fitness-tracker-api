package com.example.fitnesstracker.service;

import com.example.fitnesstracker.dto.request.LoginRequest;
import com.example.fitnesstracker.dto.request.RegisterRequest;
import com.example.fitnesstracker.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse refreshToken(String refreshToken);
}
