package com.vibeslop.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "developers")
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * A collection of skills for this developer.
     * This creates a separate table (e.g., developer_skills) to hold the developer_id and skill string.
     * Using a Set ensures that skills are unique for each developer.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "developer_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    /**
     * A developer can have multiple interviews.
     * 'mappedBy' indicates that the 'developer' field in the Interview entity owns the relationship.
     */
    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Interview> interviews = new HashSet<>();
}