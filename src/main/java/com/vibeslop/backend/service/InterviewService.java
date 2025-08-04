package com.vibeslop.backend.service;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InterviewService {

    private final OllamaChatModel chatClient;
    private final PromptTemplate promptTemplate;

    public InterviewService(OllamaChatModel chatClient,
                            @Value("classpath:/prompts/interview-questions.st") Resource interviewQuestionsPromptResource) {
        this.chatClient = chatClient;
        // The parser can be initialized once.
        this.promptTemplate = new PromptTemplate(interviewQuestionsPromptResource);
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

    public String saveInterviewFeedBack(String feedback){
        return "Succes";
    }
}