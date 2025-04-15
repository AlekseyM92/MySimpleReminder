package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j
@Component
public class VoiceHandlerImpl implements VoiceHandler {
    private final ReminderAnswerMessage reminderAnswerMessage;

    public VoiceHandlerImpl(ReminderAnswerMessage reminderAnswerMessage) {
        this.reminderAnswerMessage = reminderAnswerMessage;
    }

    @Override
    public void handleVoice(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (message == null) {
            throw new TelegramApiException("message is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("SimpleReminderBot is null");
        } else if (message.getVoice() == null) {
            throw new TelegramApiException("Voice is null");
        } else if (message.getChat() == null) {
            throw new TelegramApiException("Chat is null");
        }

        if (simpleReminderBot.getUserDataCache().getUserState(message.getChatId()) == BotStatus.CREATE_REMINDER) {
            if (message.getVoice().getDuration() > 120) {
                reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
                reminderAnswerMessage.sendErrorMessage(message
                        , "Длительность голосового сообщения не должна превышать 2 минуты!"
                        , simpleReminderBot);
            } else {
                simpleReminderBot.getUserDataCache().setUserState(message.getChatId()
                        , BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER);
                simpleReminderBot.getUserDataCache().setUserName(message.getChatId(), message.getChat().getFirstName());
                simpleReminderBot.getUserDataCache().setReminderVoiceMessage(message.getChatId(), message);
            }

        } else if (simpleReminderBot.getUserDataCache()
                .getUserState(message.getChatId()) == BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER
                || simpleReminderBot.getUserDataCache()
                .getUserState(message.getChatId()) == BotStatus.WAITING_FOR_CHOOSE_DAY
                || simpleReminderBot.getUserDataCache()
                .getUserState(message.getChatId()) == BotStatus.WAITING_FOR_CHOOSE_MONTH
                || simpleReminderBot.getUserDataCache()
                .getUserState(message.getChatId()) == BotStatus.WAITING_FOR_APPLY_DAY
                || simpleReminderBot.getUserDataCache()
                .getUserState(message.getChatId()) == BotStatus.WAITING_FOR_APPLY_MONTH) {
            reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
            reminderAnswerMessage.sendErrorMessage(message, "Вы уже отправили сообщение!\nНажмите \"Далее\"" +
                    " для продолжения или \"Отмена\" для выхода из режима создания напоминания.", simpleReminderBot);
        }
    }
}
