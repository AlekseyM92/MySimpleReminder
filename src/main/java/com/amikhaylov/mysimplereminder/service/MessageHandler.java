package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MessageHandler {
    public void handleMessage(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
