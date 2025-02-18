package com.tolmic.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LLMUsage {

    @Autowired
    private ChatClient chatClient;

    @SuppressWarnings("null")
    public String getAnswer(String prompt) {
        String answer = chatClient
                            .prompt()
                            .user(prompt)
                            .call()
                            .content().replace("*", "");

        return answer;
    }

}
