package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ReminderAnswerCallbackImpl implements ReminderAnswerCallback {
    @Override
    public void answerCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .build()
        );
    }

    @Override
    public void answerCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot
            , String message, Boolean showAlert) throws TelegramApiException {
        simpleReminderBot.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(message)
                .showAlert(showAlert)
                .build()
        );
    }

    @Override
    public void deleteUserErrorMessageIfPresent(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (simpleReminderBot.getUserDataCache().errorMessageIsPresent(callbackQuery.getMessage().getChatId())) {
            simpleReminderBot.execute(DeleteMessage.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(simpleReminderBot.getUserDataCache()
                            .getUserErrorMessage(callbackQuery.getMessage().getChatId())
                            .getMessageId()
                    ).build()
            );
            simpleReminderBot.getUserDataCache().deleteUserErrorMessage(callbackQuery.getMessage().getChatId());
        }
    }

    @Override
    public void deleteCallbackMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(DeleteMessage.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId())
                .build());
    }
}
