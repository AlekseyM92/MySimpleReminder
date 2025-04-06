package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface VoiceHandler {
    public void handleVoice(Chat chat, Voice voice, SimpleReminderBot simpleReminderBot) throws TelegramApiException;
}
