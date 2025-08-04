package com.vibeslop.backend.repository;

import com.vibeslop.backend.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    List<Developer> findBySkillsContaining(String skill);

    @Query("SELECT d FROM Developer d WHERE :skillCount = (SELECT COUNT(s) FROM d.skills s WHERE s IN :skills)")
    List<Developer> findByAllSkills(@Param("skills") List<String> skills, @Param("skillCount") Long skillCount);

}