package com.tolmic.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.tolmic.llm.LLMUsage;
import com.tolmic.service.impl.VacanciesService;

// import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import com.tolmic.api.hh.HHApi;
import com.tolmic.entity.Vacancy;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    @Autowired
    private LLMUsage LLMUsage;

    @Autowired
    private HHApi HHApi;

    @Autowired
    private VacanciesService vacanciesService;

    @Autowired
    private EmbeddingModel embeddingModel;

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
    public void onUpdateReceived(Update update) {
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
                EmbeddingResponse vector = embeddingModel.embedForResponse(List.of(messageText));

                List<Object[]> vacancies = vacanciesService.findVacanciesFromDB(vector.getResults().get(0).getOutput());
                List<Vacancy> vacs = new ArrayList<>();

                for (Object[] vacancy : vacancies) {
                    if ((Double) vacancy[1] > 0.80) {
                        // vacs.add(vacanciesService.fromObject(vacancy[0]));
                    }
                }

                int a = 10;
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
            writeLog("Reply sent");
        } catch (TelegramApiException e) {
            writeLog(e.getMessage());
        }
    }

    private void writeLog(String messege) {
        log.info(messege);
    }

    private void sendPhoto(Long chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        // to do, don't need any dependency
    }

}