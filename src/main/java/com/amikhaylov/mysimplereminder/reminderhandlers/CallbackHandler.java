package com.amikhaylov.mysimplereminder.reminderhandlers;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallbackHandler {
    public void handleCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
