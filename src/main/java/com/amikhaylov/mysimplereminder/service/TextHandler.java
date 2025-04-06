package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TextHandler {
    public void handleText(Chat chat, String text, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
