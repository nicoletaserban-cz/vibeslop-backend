package com.vibeslop.backend.service;

import com.vibeslop.backend.dto.DeveloperDetailDto;
import com.vibeslop.backend.dto.InterviewSummaryDto;
import com.vibeslop.backend.exception.ResourceNotFoundException;
import com.vibeslop.backend.model.Developer;
import com.vibeslop.backend.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for generating greetings.
 * The @Service annotation marks this as a Spring-managed component, making it
 * eligible for dependency injection into other components like controllers.
 */
@RequiredArgsConstructor
@Service
public class DeveloperService {

    // FIX: Made the repository private and final for proper DI and encapsulation.
    private final DeveloperRepository developerRepository;
    /**
     * Finds developers who have AT LEAST ONE of the skills from the provided list.
     * Renamed from getDevelopers for clarity and consistency.
     * NOTE: This implementation makes a separate DB call for each skill, which can be inefficient.
     * For better performance, this could be refactored into a single custom JPQL query.
     */
    @Transactional(readOnly = true)
    public List<Developer> findDevelopersByAnySkill(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }
        return skills.stream()
                .flatMap(skill -> developerRepository.findBySkillsContaining(skill).stream())
                .distinct().toList();
    }

    @Transactional(readOnly = true)
    public List<Developer> findDevelopersByAllSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }
        // We pass the size of the list as a parameter to the custom query.
        return developerRepository.findByAllSkills(skills, (long) skills.size());
    }

    /**
     * Finds a single developer by their ID.
     * FIX: Throws a custom ResourceNotFoundException if the developer does not exist,
     * preventing NullPointerExceptions and providing a clean 404 response.
     */
    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloper(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id: " + id));

        // Map the found Developer entity to the DeveloperDetailDto
        return new DeveloperDetailDto(
                developer.getId(),
                developer.getName(),
                developer.getSkills(), // Direct mapping as it's a Set<String>
                developer.getInterviews().stream()
                        .map(interview -> new InterviewSummaryDto(
                                interview.getInterviewId(),
                                interview.getClient().getName(), // Safely access related entity
                                interview.getFeedback()
                        ))
                        .collect(Collectors.toSet())
        );
    }
}