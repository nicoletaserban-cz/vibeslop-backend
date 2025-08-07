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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private OllamaChatModel chatClient;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PromptTemplate interviewQuestionsPromptTemplate;

    @InjectMocks
    private InterviewService interviewService;

    private Developer developer;
    private Client client;
    private Interview interview;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(1L);
        developer.setName("Alice Johnson");

        client = new Client();
        client.setClientId(2L);
        client.setName("TechCorp");

        interview = new Interview();
        interview.setInterviewId(10L);
        interview.setDeveloper(developer);
        interview.setClient(client);
        interview.setFeedback("Great candidate.");
    }

    @Test
    void generateQuestions_shouldReturnStringFromAI() {
        List<String> technologies = List.of("Java", "Spring");
        Prompt prompt = new Prompt("test prompt");
        String expectedResponse = "Here are some questions...";
        Generation generation = new Generation(new AssistantMessage(expectedResponse));
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(interviewQuestionsPromptTemplate.create(Map.of("technologies", "Java, Spring"))).thenReturn(prompt);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        String actualResponse = interviewService.generateQuestions(technologies);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void createInterview_whenValid_shouldReturnDto() {
        InterviewRequestDto requestDto = new InterviewRequestDto(1L, 2L, "Great candidate.");
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(clientRepository.findById(2L)).thenReturn(Optional.of(client));
        when(interviewRepository.save(any(Interview.class))).thenReturn(interview);

        InterviewResponseDto responseDto = interviewService.createInterview(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.interviewId()).isEqualTo(10L);
        assertThat(responseDto.developerName()).isEqualTo("Alice Johnson");
        assertThat(responseDto.clientName()).isEqualTo("TechCorp");
        verify(interviewRepository).save(any(Interview.class));
    }

    @Test
    void createInterview_whenDeveloperNotFound_shouldThrowException() {
        InterviewRequestDto requestDto = new InterviewRequestDto(99L, 2L, "Feedback");
        when(developerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> interviewService.createInterview(requestDto));
    }
}