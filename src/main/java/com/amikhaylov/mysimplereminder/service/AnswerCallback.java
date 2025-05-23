package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public interface AnswerCallback {
    public void answerCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void answerCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot
            , String message, Boolean showAlert) throws TelegramApiException;

    public void deleteUserErrorMessageIfPresent(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void deleteLastBotMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void deleteRemindersMessages(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void deleteCallbackMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;
}
