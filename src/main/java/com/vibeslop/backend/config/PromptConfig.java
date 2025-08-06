package com.vibeslop.backend.config;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class PromptConfig {

    @Bean
    public PromptTemplate interviewQuestionsPromptTemplate(
        @Value("${spring.ai.prompt.interview-questions.path}") Resource resource) {
            return new PromptTemplate(resource);
        }
}