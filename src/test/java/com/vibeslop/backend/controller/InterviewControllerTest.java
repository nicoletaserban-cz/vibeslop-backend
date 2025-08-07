package com.vibeslop.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibeslop.backend.dto.InterviewQuestionRequest;
import com.vibeslop.backend.dto.InterviewRequestDto;
import com.vibeslop.backend.dto.InterviewResponseDto;
import com.vibeslop.backend.exception.ResourceNotFoundException;
import com.vibeslop.backend.repository.UserRepository;
import com.vibeslop.backend.service.InterviewService;
import com.vibeslop.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InterviewService interviewService;

    // These are needed by the security configuration but not directly used in these tests
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository; // Added to allow ApplicationConfig to load

   /* @Test
    @WithMockUser
    void generateInterviewQuestions_shouldReturnGeneratedText() throws Exception {
        InterviewQuestionRequest request = new InterviewQuestionRequest(List.of("Java", "Spring"));
        String expectedResponse = "Here are some questions about Java and Spring...";

        given(interviewService.generateQuestions(request.technologies())).willReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/interviews/generate")
                        .with(csrf()) // Add CSRF token to the request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }*/

    @Test
    @WithMockUser
    void createInterview_withValidData_shouldReturnCreated() throws Exception {
        InterviewRequestDto requestDto = new InterviewRequestDto(1L, 2L, "Great candidate.");
        InterviewResponseDto responseDto = new InterviewResponseDto(
                10L,
                1L, "Alice Johnson",
                2L, "TechCorp",
                "Great candidate."
        );

        given(interviewService.createInterview(any(InterviewRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/v1/interviews")
                        .with(csrf()) // Add CSRF token to the request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/interviews/10")))
                .andExpect(jsonPath("$.interviewId", is(10)))
                .andExpect(jsonPath("$.developerName", is("Alice Johnson")))
                .andExpect(jsonPath("$.clientName", is("TechCorp")));
    }

    @Test
    @WithMockUser
    void createInterview_whenDeveloperNotFound_shouldReturnNotFound() throws Exception {
        InterviewRequestDto requestDto = new InterviewRequestDto(99L, 2L, "Feedback");

        given(interviewService.createInterview(any(InterviewRequestDto.class)))
                .willThrow(new ResourceNotFoundException("Developer not found with id: 99"));

        mockMvc.perform(post("/api/v1/interviews")
                        .with(csrf()) // Add CSRF token to the request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }
}