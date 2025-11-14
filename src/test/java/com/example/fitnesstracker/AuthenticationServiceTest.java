package com.example.fitnesstracker;

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
import com.example.fitnesstracker.service.impl.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("register should save user and return tokens when email is available")
    void register_whenEmailIsAvailable_shouldSaveUserAndReturnTokens() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

        AuthenticationResponse response = authenticationService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");

        verify(userRepository).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register should throw EmailAlreadyExistsException when email is taken")
    void register_whenEmailIsTaken_shouldThrowEmailAlreadyExistsException() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email " + registerRequest.getEmail() + " is already taken");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("login should authenticate user and return tokens for valid credentials")
    void login_whenCredentialsAreValid_shouldReturnTokens() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token");

        AuthenticationResponse response = authenticationService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
    }

    @Test
    @DisplayName("refreshToken should return new tokens for a valid refresh token")
    void refreshToken_whenTokenIsValid_shouldReturnNewTokens() {
        String validRefreshToken = "valid_refresh_token";
        when(jwtService.extractUsername(validRefreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(validRefreshToken, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new_refresh_token");

        AuthenticationResponse response = authenticationService.refreshToken(validRefreshToken);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new_access_token");
        assertThat(response.getRefreshToken()).isEqualTo("new_refresh_token");
        verify(jwtService).extractUsername(validRefreshToken);
        verify(userRepository).findByEmail(user.getEmail());
        verify(jwtService).isTokenValid(validRefreshToken, user);
    }

    @Test
    @DisplayName("refreshToken should throw InvalidTokenException if username cannot be extracted")
    void refreshToken_whenUsernameIsNull_shouldThrowInvalidTokenException() {
        String invalidToken = "invalid_token";
        when(jwtService.extractUsername(invalidToken)).thenReturn(null);

        assertThatThrownBy(() -> authenticationService.refreshToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid Refresh Token");
    }

    @Test
    @DisplayName("refreshToken should throw ResourceNotFoundException if user does not exist")
    void refreshToken_whenUserNotFound_shouldThrowResourceNotFoundException() {
        String tokenForNonExistentUser = "token_for_non_existent_user";
        when(jwtService.extractUsername(tokenForNonExistentUser)).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken(tokenForNonExistentUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User associated with this token not found");
    }

    @Test
    @DisplayName("refreshToken should throw InvalidTokenException if token is not valid for the user")
    void refreshToken_whenTokenIsNotValid_shouldThrowInvalidTokenException() {
        String expiredToken = "expired_token";
        when(jwtService.extractUsername(expiredToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(expiredToken, user)).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.refreshToken(expiredToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid Refresh Token");
    }
}