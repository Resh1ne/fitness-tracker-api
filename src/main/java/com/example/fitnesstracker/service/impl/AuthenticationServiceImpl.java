package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.dto.request.LoginRequest;
import com.example.fitnesstracker.dto.request.RegisterRequest;
import com.example.fitnesstracker.dto.response.AuthenticationResponse;
import com.example.fitnesstracker.entity.User;
import com.example.fitnesstracker.entity.enums.Role;
import com.example.fitnesstracker.exception.EmailAlreadyExistsException;
import com.example.fitnesstracker.exception.InvalidTokenException;
import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.repository.UserRepository;
import com.example.fitnesstracker.security.JwtService;
import com.example.fitnesstracker.security.UserDetailsImpl;
import com.example.fitnesstracker.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
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

        UserDetails userDetails = new UserDetailsImpl(user);
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserDetails userDetails = new UserDetailsImpl(user);
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }

        var user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User associated with this token not found"));

        UserDetails userDetails = new UserDetailsImpl(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidTokenException("Invalid Refresh Token");
        }

        var accessToken = jwtService.generateToken(userDetails);
        var newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
