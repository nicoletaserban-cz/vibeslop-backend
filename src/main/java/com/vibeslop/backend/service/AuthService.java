package com.vibeslop.backend.service;

import com.vibeslop.backend.dto.AuthRequestDto;
import com.vibeslop.backend.dto.AuthResponseDto;
import com.vibeslop.backend.dto.RegisterRequestDto;
import com.vibeslop.backend.exception.UserAlreadyExistsException;
import com.vibeslop.backend.model.User;
import com.vibeslop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken.");
                });

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication: " + request.getUsername()));
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);
    }
}