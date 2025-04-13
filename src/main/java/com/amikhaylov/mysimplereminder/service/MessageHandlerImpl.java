package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j
@Component
@Getter
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
        if (message.hasText()) {
            textHandler.handleText(message, simpleReminderBot);
        } else if (message.hasVoice()) {
            voiceHandler.handleVoice(message, simpleReminderBot);
        }
    }
}
