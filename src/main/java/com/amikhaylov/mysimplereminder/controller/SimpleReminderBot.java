package com.amikhaylov.mysimplereminder.controller;

import com.amikhaylov.mysimplereminder.cache.UserDataCache;
import com.amikhaylov.mysimplereminder.config.BotConfig;
import com.amikhaylov.mysimplereminder.service.CallbackHandler;
import com.amikhaylov.mysimplereminder.service.MenuCommands;
import com.amikhaylov.mysimplereminder.service.MessageHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
@Getter
public class SimpleReminderBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CallbackHandler callbackHandler;
    private final MessageHandler messageHandler;
    private final MenuCommands menuCommands;
    private final UserDataCache userDataCache;


    @Autowired
    public SimpleReminderBot(
            BotConfig botConfig,
            CallbackHandler callbackHandler,
            MessageHandler messageHandler,
            MenuCommands menuCommands,
            UserDataCache userDataCache) {

        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.callbackHandler = callbackHandler;
        this.messageHandler = messageHandler;
        this.menuCommands = menuCommands;
        this.userDataCache = userDataCache;

        try {
            menuCommands.initMenu(this);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                callbackHandler.handleCallback(update.getCallbackQuery(), this);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
        if (update.hasMessage()) {
            try {
                messageHandler.handleMessage(update.getMessage(), this);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }

    public String getCurrentBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}