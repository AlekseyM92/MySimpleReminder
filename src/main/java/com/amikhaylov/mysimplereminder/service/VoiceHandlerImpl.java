package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

@Log4j
@Component
public class VoiceHandlerImpl implements VoiceHandler {

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
                deleteMessage(message, simpleReminderBot);
                sendErrorMessage(message, "Длительность голосового сообщения не должна превышать 2 минуты!"
                        , simpleReminderBot);
            } else {
                simpleReminderBot.getUserDataCache().setUserState(message.getChatId()
                        , BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER);
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
            deleteMessage(message, simpleReminderBot);
            sendErrorMessage(message, "Вы уже отправили сообщение!\nНажмите \"Далее\"" +
                    " для продолжения или \"Отмена\" для выхода из режима создания напоминания.", simpleReminderBot);
        }
    }

    private void sendErrorMessage(Message message, String answer, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (simpleReminderBot.getUserDataCache().errorMessageIsPresent(message.getChatId())) {
            deleteMessage(simpleReminderBot.getUserDataCache().getUserErrorMessage(message.getChatId())
                    , simpleReminderBot);
            simpleReminderBot.getUserDataCache()
                    .setUserErrorMessage(sendAnswerMessage(message, answer, simpleReminderBot));
        } else {
            simpleReminderBot.getUserDataCache()
                    .setUserErrorMessage(sendAnswerMessage(message, answer, simpleReminderBot));
        }
    }

    public void sendAnswerVoice(Message message, String path, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(SendVoice.builder()
                .chatId(message.getChatId())
                .voice(new InputFile(new File(path)))
                .build()
        );
    }

    private Message sendAnswerMessage(Message message, String answer, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        return simpleReminderBot.execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text(answer)
                .build());
    }

    private void deleteMessage(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(DeleteMessage.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .build()
        );
    }

    private void deleteMessages(Long chatId, List<Integer> messageIds, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(DeleteMessages.builder()
                .chatId(chatId)
                .messageIds(messageIds)
                .build()
        );
    }
}
