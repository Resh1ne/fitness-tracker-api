package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.dto.AuthenticationResponse;
import com.example.fitnesstracker.dto.LoginRequest;
import com.example.fitnesstracker.dto.RegisterRequest;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.entity.enums.Role;
import com.example.fitnesstracker.exception.EmailAlreadyExistsException;
import com.example.fitnesstracker.exception.InvalidTokenException;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + request.getEmail() + " is already taken");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }

        var user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User associated with this token not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }

        var accessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
