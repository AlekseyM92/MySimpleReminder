package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallbackHandlerImpl implements CallbackHandler {
    @Override
    public void handleCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (callbackQuery == null) {
            throw new TelegramApiException("Callback query is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("SimpleReminderBot is null");
        }
        var callbackData = callbackQuery.getData();
        if (callbackData != null) {
            switch (callbackData) {
                case "cancel":
                    simpleReminderBot.getUserDataCache()
                            .setUserState(callbackQuery.getMessage().getChatId(), BotStatus.DEFAULT);
                    AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQuery.getId()).build();
                    simpleReminderBot.execute(answerCallbackQuery);
                    simpleReminderBot.execute(SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId())
                            .text("Отмена")
                            .build()
                    );
                    simpleReminderBot.execute(SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId())
                            .text("Для создания напоминания выберете команду /create_reminder.\n"
                                    + "Для вызова подсказки по управлению ботом выберете команду /help\n"
                                    + "Для вывода описания функционала бота выберете команду /description.")
                            .build()
                    );
                    simpleReminderBot.execute(DeleteMessage.builder()
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .chatId(callbackQuery.getMessage().getChatId())
                            .build()
                    );
                    break;
            }
        }
    }
}
