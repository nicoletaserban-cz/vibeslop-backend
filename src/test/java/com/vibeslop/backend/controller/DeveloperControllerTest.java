package com.vibeslop.backend.controller;

import com.vibeslop.backend.dto.DeveloperDetailDto;
import com.vibeslop.backend.dto.InterviewSummaryDto;
import com.vibeslop.backend.exception.ResourceNotFoundException;
import com.vibeslop.backend.repository.UserRepository;
import com.vibeslop.backend.service.DeveloperService;
import com.vibeslop.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeveloperController.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperService developerService;

    // These are needed by the security configuration but not directly used in these tests
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository; // Added to allow ApplicationConfig to load

    private DeveloperDetailDto developerDetailDto;

    @BeforeEach
    void setUp() {
        developerDetailDto = new DeveloperDetailDto(
                1L,
                "Alice Johnson",
                Set.of("Java", "Spring"),
                Set.of(new InterviewSummaryDto(1L, "TechCorp", "Great candidate"))
        );
    }

    @Test
    @WithMockUser // Bypasses JWT authentication for this test
    void getDeveloper_whenDeveloperExists_shouldReturnDeveloperDetails() throws Exception {
        given(developerService.getDeveloper(1L)).willReturn(developerDetailDto);

        mockMvc.perform(get("/api/v1/developers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice Johnson")));
    }

    @Test
    @WithMockUser
    void getDeveloper_whenDeveloperDoesNotExist_shouldReturnNotFound() throws Exception {
        given(developerService.getDeveloper(anyLong())).willThrow(new ResourceNotFoundException("Developer not found"));

        mockMvc.perform(get("/api/v1/developers/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findDevelopersBySkills_withMatchAll_shouldReturnMatchingDevelopers() throws Exception {
        List<DeveloperDetailDto> developers = List.of(developerDetailDto);
        given(developerService.findDevelopersByAllSkills(List.of("Java", "Spring"))).willReturn(developers);

        mockMvc.perform(get("/api/v1/developers")
                        .param("skills", "Java,Spring")
                        .param("match", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")));
    }

    @Test
    @WithMockUser
    void findDevelopersBySkills_withMatchAny_shouldReturnMatchingDevelopers() throws Exception {
        List<DeveloperDetailDto> developers = List.of(developerDetailDto);
        given(developerService.findDevelopersByAnySkill(List.of("Java"))).willReturn(developers);

        mockMvc.perform(get("/api/v1/developers")
                        .param("skills", "Java")
                        .param("match", "any"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")));
    }
}