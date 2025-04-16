package com.amikhaylov.mysimplereminder.reminderhandlers;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TextHandler {
    public void handleText(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
