package com.vibeslop.backend.controller;

import com.vibeslop.backend.dto.DeveloperDetailDto;
import com.vibeslop.backend.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for querying Developer resources.
 */
@RestController
// FIX: Using a versioned, plural noun for the resource path is a REST best practice.
@RequestMapping("/api/v1/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * Finds a single developer by their unique ID.
     * The path is now distinct and clear: GET /api/v1/developers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeveloperDetailDto> getDeveloper(@PathVariable Long id) {
        return ResponseEntity.ok(developerService.getDeveloper(id));
    }

    /**
     * Find all developers.
     *
     * @return A list of DTOs representing all developers.
     */
    @GetMapping("/all")
    public ResponseEntity<List<DeveloperDetailDto>> getDevelopers() {
        return ResponseEntity.ok(developerService.getAllDevelopers());
    }

    /**
     * Finds developers by skills. This single endpoint handles finding developers
     * with ANY or ALL of the provided skills, based on the 'match' parameter.
     * The path is the root of the resource: GET /api/v1/developers
     * Example (ALL): GET /api/v1/developers?skills=Java,Docker
     * Example (ANY): GET /api/v1/developers?skills=Java,Docker&match=any
     */
    @GetMapping
    public ResponseEntity<List<DeveloperDetailDto>> findDevelopersBySkills(
            @RequestParam List<String> skills,
            @RequestParam(defaultValue = "all", required = false) String match) {

        List<DeveloperDetailDto> developers = "any".equalsIgnoreCase(match)
                ? developerService.findDevelopersByAnySkill(skills)
                : developerService.findDevelopersByAllSkills(skills);

        return ResponseEntity.ok(developers);
    }
}
