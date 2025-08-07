package com.vibeslop.backend.service;

import com.vibeslop.backend.dto.AuthRequestDto;
import com.vibeslop.backend.dto.AuthResponseDto;
import com.vibeslop.backend.dto.RegisterRequestDto;
import com.vibeslop.backend.exception.UserAlreadyExistsException;
import com.vibeslop.backend.model.User;
import com.vibeslop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameIsNew_shouldSaveUserAndReturnToken() {
        RegisterRequestDto request = new RegisterRequestDto("newUser", "password");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mock-jwt-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_whenUsernameExists_shouldThrowException() {
        RegisterRequestDto request = new RegisterRequestDto("existingUser", "password");
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_whenCredentialsAreValid_shouldReturnToken() {
        AuthRequestDto request = new AuthRequestDto("user", "password");
        User user = User.builder().username("user").password("encodedPassword").build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("mock-jwt-token");
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("user", "password"));
    }

    @Test
    void authenticate_whenCredentialsAreInvalid_shouldThrowException() {
        AuthRequestDto request = new AuthRequestDto("user", "wrong-password");
        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }
}