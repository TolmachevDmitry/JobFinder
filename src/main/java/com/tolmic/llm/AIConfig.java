package com.tolmic.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AIConfig {

    @Bean
    public EmbeddingModel embeddingModel(EmbeddingModel embeddingModel) {
        return embeddingModel;
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                        .defaultSystem("You are chat, that must help user to change and pick up professions, sphere and vacancies. " +
                                        "User can ask you about existing vacancies, them requirements, description and experiences. " +
                                        "User want to know actual requirements to understad, what he need to be competent, and to plan " +
                                        "his learning process. User want to find suitable vacancies, corresponding to his resume. " +
                                        "You should not respond to extraneous topics. " +
                                        "You must speak only Russian. " + 
                                        "Don't answer on meaningless user's messegers. " +
                                        "If user wrote just one-two words, then clarify what he wants cpecifically.")
                        .build();
    }

}
