package com.vibeslop.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibeslop.backend.dto.AuthRequestDto;
import com.vibeslop.backend.dto.AuthResponseDto;
import com.vibeslop.backend.dto.RegisterRequestDto;
import com.vibeslop.backend.exception.UserAlreadyExistsException;
import com.vibeslop.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_withValidData_shouldReturnToken() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto("newUser", "password");
        AuthResponseDto authResponse = new AuthResponseDto("mock-jwt-token");

        given(authService.register(any(RegisterRequestDto.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")));
    }

    @Test
    void register_whenUserExists_shouldReturnConflict() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto("existingUser", "password");

        given(authService.register(any(RegisterRequestDto.class)))
                .willThrow(new UserAlreadyExistsException("Username 'existingUser' is already taken."));

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void authenticate_withValidCredentials_shouldReturnToken() throws Exception {
        AuthRequestDto authRequest = new AuthRequestDto("user", "password");
        AuthResponseDto authResponse = new AuthResponseDto("mock-jwt-token");

        given(authService.authenticate(any(AuthRequestDto.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mock-jwt-token")));
    }

    @Test
    void authenticate_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        AuthRequestDto authRequest = new AuthRequestDto("user", "wrong-password");

        given(authService.authenticate(any(AuthRequestDto.class)))
                .willThrow(new BadCredentialsException("Invalid username or password."));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}