package com.amikhaylov.mysimplereminder.controller;

import com.amikhaylov.mysimplereminder.config.BotConfig;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
public class SimpleReminderBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public SimpleReminderBot(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать общение с ботом"));
        listOfCommands.add(new BotCommand("/mydata", "Получить мои персональные данные"));
        listOfCommands.add(new BotCommand("/deletedata", "Удалить мои данные"));
        listOfCommands.add(new BotCommand("/help", "Справка по управлению ботом"));
        listOfCommands.add(new BotCommand("/settings", "Настройки бота"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
        var messageText = message.getText();
        log.debug(message.getText());

        if (!messageText.isEmpty()) {
            var response = new SendMessage();
            response.setChatId(message.getChatId().toString());

            switch (messageText) {
                case "/start":
                    response.setText("Привет " + message.getChat().getFirstName() + "!\n"
                            + "Вы нажали start");
                    break;
                case "/mydata":
                    response.setText("Вы нажали mydata");
                    break;
                case "/deletedata":
                    response.setText("Вы нажали deletedata");
                    break;
                case "/help":
                    response.setText("Вы нажали help");
                    break;
                case "/settings":
                    response.setText("Вы нажали settings");
                    break;
                default:
                    response.setText("Вы ввели " + messageText);
                    break;
            }
            sendAnswerMessage(response);
        }
    }

    public String getCurrentBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
