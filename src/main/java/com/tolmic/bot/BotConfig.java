package com.tolmic.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@PropertySource("application.yml")
@Configuration
public class BotConfig {
    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.chatId}")
    private Long chatId;

    @Bean
    public Bot bot() {
        return new Bot(token, name);
    }
}
