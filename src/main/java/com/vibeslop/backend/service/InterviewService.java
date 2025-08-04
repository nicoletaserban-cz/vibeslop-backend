package com.vibeslop.backend.service;

import com.vibeslop.backend.dto.InterviewRequestDto;
import com.vibeslop.backend.dto.InterviewResponseDto;
import com.vibeslop.backend.exception.ResourceNotFoundException;
import com.vibeslop.backend.model.Client;
import com.vibeslop.backend.model.Developer;
import com.vibeslop.backend.model.Interview;
import com.vibeslop.backend.repository.ClientRepository;
import com.vibeslop.backend.repository.DeveloperRepository;
import com.vibeslop.backend.repository.InterviewRepository;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service

public class InterviewService {

    private final OllamaChatModel chatClient;
    private final PromptTemplate promptTemplate;
    private final InterviewRepository interviewRepository;
    private final DeveloperRepository developerRepository;
    private final ClientRepository clientRepository;

    public InterviewService(OllamaChatModel chatClient,
                            @Value("classpath:/prompts/interview-questions.st") Resource interviewQuestionsPromptResource, InterviewRepository interviewRepository, DeveloperRepository developerRepository, ClientRepository clientRepository) {
        this.chatClient = chatClient;
        // The parser can be initialized once.
        this.promptTemplate = new PromptTemplate(interviewQuestionsPromptResource);
        this.interviewRepository = interviewRepository;
        this.developerRepository = developerRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Generates interview questions based on a list of technologies.*
     * This implementation uses Spring AI to connect to a self-hosted
     * Ollama model to generate the questions.
     * @param technologies A list of technologies like "Java", "Spring Boot", etc.
     * @return A list of generated interview questions.
     */
    public String generateQuestions(List<String> technologies) {
        Prompt prompt = this.promptTemplate.create(Map.of(
                "technologies", String.join(", ", technologies)
        ));

        return chatClient.call(prompt).getResult().getOutput().getText();
    }

    /**
     * Creates a new interview record based on the provided request data.
     *
     * @param requestDto DTO containing the developer ID, client ID, and feedback.
     * @return A DTO representing the newly created interview.
     * @throws ResourceNotFoundException if the developer or client does not exist.
     */
    @Transactional
    public InterviewResponseDto createInterview(InterviewRequestDto requestDto) {
        // Find the related developer, or throw an exception
        Developer developer = developerRepository.findById(requestDto.developerId())
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id: " + requestDto.developerId()));

        // Find the related client, or throw an exception
        Client client = clientRepository.findById(requestDto.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + requestDto.clientId()));

        // Create and save the new interview
        Interview interview = new Interview();
        interview.setDeveloper(developer);
        interview.setClient(client);
        interview.setFeedback(requestDto.feedback());

        Interview savedInterview = interviewRepository.save(interview);

        // Map the saved entity to a response DTO
        return new InterviewResponseDto(
                savedInterview.getInterviewId(),
                developer.getId(), developer.getName(),
                client.getClientId(), client.getName(),
                savedInterview.getFeedback()
        );
    }
}