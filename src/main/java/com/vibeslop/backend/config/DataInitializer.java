package com.vibeslop.backend.config;

import com.vibeslop.backend.model.Client;
import com.vibeslop.backend.model.Developer;
import com.vibeslop.backend.model.Interview;
import com.vibeslop.backend.repository.ClientRepository;
import com.vibeslop.backend.repository.DeveloperRepository;
import com.vibeslop.backend.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * This component runs on application startup to populate the database with initial data.
 * It's useful for development and demonstration purposes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DeveloperRepository developerRepository;
    private final ClientRepository clientRepository;
    private final InterviewRepository interviewRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (developerRepository.count() > 0) {
            log.info("Database already contains data. Skipping initialization.");
            return;
        }

        log.info("Initializing database with sample data...");

        // Create and save developers
        Developer dev1 = new Developer();
        dev1.setName("Alice Johnson");
        dev1.setSkills(Set.of("Java", "Spring Boot", "PostgreSQL", "Docker"));
        developerRepository.save(dev1);

        Developer dev2 = new Developer();
        dev2.setName("Bob Williams");
        dev2.setSkills(Set.of("Spring", "Django", "React", "AWS", "Docker"));
        developerRepository.save(dev2);

        Developer dev3 = new Developer();
        dev3.setName("Bob Williams");
        dev3.setSkills(Set.of("Spring Boot", "Docker", "React", "AWS"));
        developerRepository.save(dev3);

        // Create and save clients
        Client client1 = new Client();
        client1.setName("TechCorp");
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setName("Innovate Inc.");
        clientRepository.save(client2);

        // Create and save an interview linking a developer and a client
        Interview interview1 = new Interview();
        interview1.setDeveloper(dev1);
        interview1.setClient(client1);
        interview1.setFeedback("Excellent Java knowledge and problem-solving skills. Strong candidate.");
        interviewRepository.save(interview1);

        // Create another interview
        Interview interview2 = new Interview();
        interview2.setDeveloper(dev2);
        interview2.setClient(client1);
        interview2.setFeedback("Good React skills, but could use more backend experience for this role.");
        interviewRepository.save(interview2);

        Interview interview3 = new Interview();
        interview3.setDeveloper(dev1);
        interview3.setClient(client2);
        interview3.setFeedback("Excellent Java knowledge. No AWS experience");
        interviewRepository.save(interview3);

        log.info("Database initialization complete.");
    }
}