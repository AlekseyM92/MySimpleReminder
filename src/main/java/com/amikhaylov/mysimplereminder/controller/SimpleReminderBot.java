package com.amikhaylov.mysimplereminder.controller;

import com.amikhaylov.mysimplereminder.cache.UserDataCache;
import com.amikhaylov.mysimplereminder.config.BotConfig;
import com.amikhaylov.mysimplereminder.config.PropertiesConfig;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import com.amikhaylov.mysimplereminder.service.CallbackHandler;
import com.amikhaylov.mysimplereminder.service.MessageHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
@Getter
public class SimpleReminderBot extends TelegramLongPollingBot {


    private final BotConfig botConfig;
    private final PropertiesConfig propertiesConfig;
    private final CallbackHandler callbackHandler;
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    private final UserDataCache userDataCache;
    private final MessageHandler messageHandler;


    @Autowired
    public SimpleReminderBot(
            BotConfig botConfig,
            PropertiesConfig propertiesConfig,
            CallbackHandler callbackHandler,
            ReminderInlineKeyboards reminderInlineKeyboards,
            UserDataCache userDataCache,
            MessageHandler messageHandler) {

        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.propertiesConfig = propertiesConfig;
        this.callbackHandler = callbackHandler;
        this.reminderInlineKeyboards = reminderInlineKeyboards;
        this.userDataCache = userDataCache;
        this.messageHandler = messageHandler;

        List<BotCommand> listOfCommands = new ArrayList<>();
        propertiesConfig.getDescriptions().keySet()
                .forEach(k -> {
                    listOfCommands.add(new BotCommand(k, propertiesConfig.getDescriptions().get(k)));
                });

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();
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