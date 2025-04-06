package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MessageHandlerImpl implements MessageHandler {
    TextHandler textHandler;
    VoiceHandler voiceHandler;
    @Autowired
    private MessageHandlerImpl(TextHandler textHandler, VoiceHandler voiceHandler) {
        this.textHandler = textHandler;
        this.voiceHandler = voiceHandler;
    }
    @Override
    public void handleMessage(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (message == null) {
            throw new TelegramApiException("Message is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("Message is null");
        }
        Chat chat = message.getChat();
        Long chatId = chat.getId();
        if (message.hasText()) {
            String text = message.getText();
            if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.DEFAULT) {
                textHandler.handleText(chat, text, simpleReminderBot);
            }
        } else if (message.hasVoice()) {
            Voice voice = message.getVoice();
            voiceHandler.handleVoice(chat, voice, simpleReminderBot);
        }
    }
}
