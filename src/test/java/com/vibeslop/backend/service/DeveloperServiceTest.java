package com.vibeslop.backend.service;

import com.vibeslop.backend.dto.DeveloperDetailDto;
import com.vibeslop.backend.exception.ResourceNotFoundException;
import com.vibeslop.backend.model.Developer;
import com.vibeslop.backend.repository.DeveloperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private DeveloperService developerService;

    private Developer developer1;

    @BeforeEach
    void setUp() {
        developer1 = Developer.builder()
                .id(1L)
                .name("Alice Johnson")
                .skills(Set.of("Java", "Spring"))
                .interviews(Collections.emptySet())
                .build();
    }

    @Test
    void getDeveloper_whenExists_shouldReturnDto() {
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer1));

        DeveloperDetailDto result = developerService.getDeveloper(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Alice Johnson");
        verify(developerRepository).findById(1L);
    }

    @Test
    void getDeveloper_whenNotExists_shouldThrowException() {
        when(developerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> developerService.getDeveloper(99L));

        verify(developerRepository).findById(99L);
    }

    @Test
    void findDevelopersByAnySkill_whenSkillsExist_shouldReturnDevelopers() {
        List<String> skills = List.of("Java");
        when(developerRepository.findBySkillsContaining("Java")).thenReturn(List.of(developer1));

        List<DeveloperDetailDto> results = developerService.findDevelopersByAnySkill(skills);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Alice Johnson");
        verify(developerRepository).findBySkillsContaining("Java");
    }

    @Test
    void findDevelopersByAllSkills_whenSkillsExist_shouldReturnDevelopers() {
        List<String> skills = List.of("Java", "Spring");
        when(developerRepository.findByAllSkills(skills, (long) skills.size())).thenReturn(List.of(developer1));

        List<DeveloperDetailDto> results = developerService.findDevelopersByAllSkills(skills);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Alice Johnson");
        verify(developerRepository).findByAllSkills(skills, 2L);
    }
}