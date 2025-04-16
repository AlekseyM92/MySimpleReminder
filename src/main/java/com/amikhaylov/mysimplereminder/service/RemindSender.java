package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface RemindSender {
    public void sendAllUserReminders(Message message, SimpleReminderBot simpleReminderBot);
}
