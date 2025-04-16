package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
@Getter
public class RemindSenderImpl implements RemindSender {
    private final ReminderRepositoryService reminderRepositoryService;

    @Override
    public void sendAllUserReminders(Message message, SimpleReminderBot simpleReminderBot) {

    }
}
