package com.tolmic.bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
// import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import com.tolmic.entity.Vacancy;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final int RECONNECT_PAUSE = 10000;

    @Autowired
    private LLMUsage LLMUsage;

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

                EmbeddingResponse vector = embeddingModel.embedForResponse(List.of("Что мы ждём: Опыт программирования 1С от 2-х лет; Знание 1С 7 и 8; Опыт работы с типовыми и нетиповыми конфигурациями 1С; Навыки работы с СУБД; Опыт работы с системами контроля версий Наличие сертификатов 1С будет являться преимуществом. "));
                EmbeddingResponse vector1 = embeddingModel.embedForResponse(List.of(messageText));
                float[] v = vector.getResult().getOutput();

                // List<Object[]> vacancies = vacanciesService.findVacanciesFromDB(vector.getResults().get(0).getOutput());
                // List<Vacancy> vacs = new ArrayList<>();

                // for (Object[] vacancy : vacancies) {
                //     if ((Double) vacancy[1] > 0.80) {
                //         // vacs.add(vacanciesService.fromObject(vacancy[0]));
                //     }
                // }

                // sendMessage(chatId, "Для профессии Бухгалтер в настоящее время к кандидату на должность выдвигаются следующие требования:\nВысшее экономическое образование;\nЗнание бухгалтерского и налогового учёта;\nУмение работать в программах 1С Бухгалтерия 8.3, World, Excel;\nУмение составлять квартальну отчётность;\nУмение рассчитывать заработную плату;\nРабота с первичной документацией;\nЗнание действующего законодательства и нормативных актов по бухгалтерскому учету, отчетности и анализу финансово-хозяйственной деятельности;\nВнимательность, способность работать с большим количеством документов, стрессоустойчивость.\nТребуемый опыт работы: от 1 до 2 лет.");

                sendMessage(chatId, LLMUsage.getAnswer(messageText));
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