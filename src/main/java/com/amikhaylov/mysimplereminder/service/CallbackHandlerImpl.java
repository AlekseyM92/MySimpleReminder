package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallbackHandlerImpl implements CallbackHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;

    @Autowired
    public CallbackHandlerImpl(ReminderInlineKeyboards reminderInlineKeyboards) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
    }

    @Override
    public void handleCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (callbackQuery == null) {
            throw new TelegramApiException("Callback query is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("SimpleReminderBot is null");
        }
        var callbackData = callbackQuery.getData();
        if (callbackData != null) {
            AnswerCallbackQuery answerCallbackQuery;
            switch (callbackData) {
                case "cancel":
                    simpleReminderBot.getUserDataCache()
                            .setUserState(callbackQuery.getMessage().getChatId(), BotStatus.DEFAULT);
                    answerCallbackQuery = AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQuery.getId()).build();
                    simpleReminderBot.execute(answerCallbackQuery);
                    simpleReminderBot.execute(SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId()).text("Отмена").build()
                    );
                    simpleReminderBot.execute(SendMessage.builder()
                            .chatId(callbackQuery.getMessage().getChatId())
                            .text("Для создания напоминания выберете команду /create_reminder.\n"
                                    + "Для вызова подсказки по управлению ботом выберете команду /help\n"
                                    + "Для вывода описания функционала бота выберете команду /description.").build()
                    );
                    simpleReminderBot.execute(DeleteMessage.builder()
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .chatId(callbackQuery.getMessage().getChatId())
                            .build()
                    );
                    break;
                case "next_step":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER) {
                        answerCallbackQuery = AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQuery.getId())
                                .build();
                        simpleReminderBot.execute(answerCallbackQuery);
                        simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                                , BotStatus.WAITING_FOR_APPLY_MONTH);
                        simpleReminderBot.execute(DeleteMessage.builder()
                                .messageId(callbackQuery.getMessage().getMessageId())
                                .chatId(callbackQuery.getMessage().getChatId())
                                .build()
                        );
                        reminderInlineKeyboards.refreshKeyboards();
                        simpleReminderBot.execute(SendMessage.builder().chatId(callbackQuery.getMessage().getChatId())
                                .text("Выберите месяц и нажмите \"Далее\"")
                                .replyMarkup(reminderInlineKeyboards.getKeyboard("months"))
                                .build());
                    } else {
                        answerCallbackQuery = AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQuery.getId())
                                .showAlert(true)
                                .text("Сначала отправьте сообщение,\n" +
                                        "затем нажмите \"Далее\"!")
                                .build();
                        simpleReminderBot.execute(answerCallbackQuery);
                    }
                    break;
                case "invalidTextCommands":
                    answerCallbackQuery = AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQuery.getId())
                            .text("Отправка команд в режиме создания напоминания запрещена!" +
                                    " Нажмете \"Отмена\" для выхода из данного режима.").showAlert(true).build();
                    simpleReminderBot.execute(answerCallbackQuery);
                    break;
                case "textIsEmpty":
                    answerCallbackQuery = AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQuery.getId())
                            .text("Сообщение не может быть пустым!").showAlert(true).build();
                    simpleReminderBot.execute(answerCallbackQuery);
                    break;
            }
        }
    }
}
