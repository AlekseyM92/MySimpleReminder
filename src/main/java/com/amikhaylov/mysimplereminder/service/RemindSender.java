package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface RemindSender {
    public void sendReminder(Reminder reminder, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
