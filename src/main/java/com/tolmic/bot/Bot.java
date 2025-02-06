package com.tolmic.bot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.tolmic.llm.LLMUsage;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;


import com.tolmic.api.hh.HHApi;
import com.tolmic.api.hh.Vacancy;


@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    @Autowired
    private LLMUsage LLMUsage;

    @Autowired
    private HHApi HHApi;

    @Value("${bot.name}")
    public String name;

    public Bot(String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return;
        }
            
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String memberName = update.getMessage().getFrom().getFirstName();
            
        switch (messageText) {
            case "/start" ->
                startBot(chatId, memberName);
            case "Привет" ->
                sendMessage(chatId, "Здравствуйте");
            case "Когда вы были созданы ?" ->
                sendMessage(chatId, "14.12.2024");
            default -> {
                try {
                    List<Vacancy> api = HHApi.getSimpleData(messageText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                String str = LLMUsage.getAnswer("Хомячки");
                sendMessage(chatId, str);
            }
        }
    }

    private void startBot(long chatId, String userName) {
        sendMessage(chatId, "Hello, " + userName + "! I'm can help you with job");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}