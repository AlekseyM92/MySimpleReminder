package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface AnswerMessage {
    public void answerMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot, String answer)
            throws TelegramApiException;

    public void answerMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot, String answer
            , ReplyKeyboard replyKeyboard) throws TelegramApiException;

    public void answerMessage(Message message, SimpleReminderBot simpleReminderBot, String answer
            , ReplyKeyboard replyKeyboard) throws TelegramApiException;

    public void answerMessage(Message message, SimpleReminderBot simpleReminderBot, String answer)
            throws TelegramApiException;

    public void deleteUserErrorMessageIfPresent(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void deleteMessage(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException;

    public void sendErrorMessage(Message message, String answer, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;
}
