package com.amikhaylov.mysimplereminder.reminderhandlers;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.service.AnswerMessage;
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
    private final AnswerMessage answerMessage;

    @Autowired
    private MessageHandlerImpl(TextHandler textHandler, VoiceHandler voiceHandler
            , AnswerMessage answerMessage) {
        this.textHandler = textHandler;
        this.voiceHandler = voiceHandler;
        this.answerMessage = answerMessage;
    }

    @Override
    public void handleMessage(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (message == null) {
            throw new TelegramApiException("Message is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("Message is null");
        }
        if (message.hasText()) {
            answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
            textHandler.handleText(message, simpleReminderBot);
        } else if (message.hasVoice()) {
            answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
            voiceHandler.handleVoice(message, simpleReminderBot);
        } else {
            answerMessage.deleteMessage(message, simpleReminderBot);
            answerMessage.sendErrorMessage(message
                    , "Поддерживаются только текстовые или голосовые сообщения!"
                    , simpleReminderBot
            );
        }
    }
}
