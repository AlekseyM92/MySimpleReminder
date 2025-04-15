package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ReminderAnswerMessageImpl implements ReminderAnswerMessage {
    @Override
    public void answerMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot, String answer)
            throws TelegramApiException {
        simpleReminderBot.execute(SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(answer)
                .build()
        );
    }

    @Override
    public void answerMessage(Message message, SimpleReminderBot simpleReminderBot, String answer)
            throws TelegramApiException {
        simpleReminderBot.execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text(answer)
                .build()
        );
    }

    @Override
    public void answerMessage(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot, String answer
            , ReplyKeyboard replyKeyboard) throws TelegramApiException {
        simpleReminderBot.execute(SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(answer)
                .replyMarkup(replyKeyboard)
                .build()
        );
    }

    @Override
    public void answerMessage(Message message, SimpleReminderBot simpleReminderBot, String answer
            , ReplyKeyboard replyKeyboard) throws TelegramApiException {
        simpleReminderBot.execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text(answer)
                .replyMarkup(replyKeyboard)
                .build()
        );
    }

    @Override
    public void deleteMessage(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        simpleReminderBot.execute(DeleteMessage.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .build()
        );
    }

    @Override
    public void deleteUserErrorMessageIfPresent(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (simpleReminderBot.getUserDataCache().errorMessageIsPresent(message.getChatId())) {
            simpleReminderBot.execute(DeleteMessage.builder()
                    .chatId(message.getChatId())
                    .messageId(simpleReminderBot.getUserDataCache()
                            .getUserErrorMessage(message.getChatId())
                            .getMessageId()
                    ).build()
            );
            simpleReminderBot.getUserDataCache().deleteUserErrorMessage(message.getChatId());
        }
    }

    @Override
    public void sendErrorMessage(Message message, String answer, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        deleteUserErrorMessageIfPresent(message, simpleReminderBot);
        simpleReminderBot.getUserDataCache()
                .setUserErrorMessage(simpleReminderBot.execute(SendMessage.builder()
                        .chatId(message.getChatId())
                        .text(answer)
                        .build())
                );
    }
}
