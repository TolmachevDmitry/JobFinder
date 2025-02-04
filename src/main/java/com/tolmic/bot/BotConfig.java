package com.tolmic.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BotConfig {

    @Value("${bot.token}")
    String botTocken;

    @Bean
    public Bot bot() {
        return new Bot(botTocken);
    }

}
