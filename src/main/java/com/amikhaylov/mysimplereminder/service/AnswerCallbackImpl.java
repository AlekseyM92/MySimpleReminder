package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AnswerCallbackImpl implements AnswerCallback {
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
    public void deleteLastBotMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (simpleReminderBot.getUserDataCache().lastBotMessageIsPresent(callbackQuery.getMessage().getChatId())) {
            simpleReminderBot.execute(DeleteMessage.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(simpleReminderBot.getUserDataCache()
                            .getlastBotMessage(callbackQuery.getMessage().getChatId())
                            .getMessageId())
                    .build());
        }
    }

    @Override
    public void deleteRemindersMessages(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        List<Map<Long, Message>> tempUserMessages = simpleReminderBot.getUserDataCache()
                .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                .filter(m -> !m.containsKey(-1L))
                .toList();
        if (!tempUserMessages.isEmpty()) {
            for (Map<Long, Message> messages : tempUserMessages) {
                if (!messages.isEmpty()) {
                    for (Message message : messages.values()) {
                        simpleReminderBot.execute(DeleteMessage.builder()
                                .chatId(callbackQuery.getMessage().getChatId())
                                .messageId(message.getMessageId())
                                .build()
                        );
                    }
                }
            }
            simpleReminderBot.getUserDataCache().deleteTempUserMessages(callbackQuery.getMessage().getChatId());
        }
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
