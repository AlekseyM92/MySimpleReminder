package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface VoiceSaver {
    public void downloadAndSaveVoiceFile(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;

    public void downloadAndSaveVoiceFile(Message message, final String fileName, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException;
}
