package com.tolmic.llm;


import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LLMUsage {

    @Autowired
    private OllamaChatModel chatModel;

    // @Autowired
    // private HuggingfaceChatModel chatModel2;

    public String getAnswer(String prompt) {
        ChatResponse response = chatModel.call(new Prompt(prompt));

        return response.getResult().getOutput().getContent();
    }

}
