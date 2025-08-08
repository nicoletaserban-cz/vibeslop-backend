package com.vibeslop.backend.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class PromptConfig {

    @Value("classpath:/prompts/interview-questions.st")
    private Resource interviewQuestionsResource;

    @Value("classpath:/prompts/interview-feedback.st")
    private Resource interviewFeedbackResource;

    @Bean
    public PromptTemplate interviewQuestionsPromptTemplate() { return new PromptTemplate(interviewQuestionsResource); }

    @Bean
    public PromptTemplate interviewFeedbackPromptTemplate() { return new PromptTemplate(interviewFeedbackResource); }
}