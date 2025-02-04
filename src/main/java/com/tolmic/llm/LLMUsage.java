package com.tolmic.llm;


import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LLMUsage {

    @Autowired
    private OllamaChatModel chatModel;

    public String getAnswer(String prompt) {
        ChatResponse response = chatModel.call(new Prompt(prompt));

        return response.getResult().getOutput().getContent().replace("*", "");
    }

}
