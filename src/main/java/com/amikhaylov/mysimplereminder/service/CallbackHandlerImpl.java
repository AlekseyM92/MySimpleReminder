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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

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
                    if (simpleReminderBot.getUserDataCache()
                            .errorMessageIsPresent(callbackQuery.getMessage().getChatId())) {
                        this.deleteMessage(simpleReminderBot.getUserDataCache()
                                .getUserErrorMessage(callbackQuery.getMessage().getChatId()), simpleReminderBot);
                        simpleReminderBot.getUserDataCache()
                                .deleteUserErrorMessage(callbackQuery.getMessage().getChatId());
                    }
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
                                , BotStatus.WAITING_FOR_CHOOSE_MONTH);
                        simpleReminderBot.execute(DeleteMessage.builder()
                                .messageId(callbackQuery.getMessage().getMessageId())
                                .chatId(callbackQuery.getMessage().getChatId())
                                .build()
                        );
                        reminderInlineKeyboards.refreshKeyboards();
                        if (simpleReminderBot.getUserDataCache()
                                .errorMessageIsPresent(callbackQuery.getMessage().getChatId())) {
                            this.deleteMessage(simpleReminderBot.getUserDataCache()
                                    .getUserErrorMessage(callbackQuery.getMessage().getChatId()), simpleReminderBot);
                            simpleReminderBot.getUserDataCache()
                                    .deleteUserErrorMessage(callbackQuery.getMessage().getChatId());
                        }
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
                case "january", "february", "march", "april", "may", "june", "july"
                , "august", "september", "october", "november", "december":
                    simpleReminderBot.getUserDataCache().setUserChoiceOfMonth(callbackQuery.getMessage().getChatId()
                            , Month.valueOf(callbackData.toUpperCase()).getValue());
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.WAITING_FOR_APPLY_MONTH);
                    answerCallbackQuery = AnswerCallbackQuery.builder()
                            .callbackQueryId(callbackQuery.getId())
                            .build();
                    simpleReminderBot.execute(answerCallbackQuery);
                    if (simpleReminderBot.getUserDataCache()
                            .errorMessageIsPresent(callbackQuery.getMessage().getChatId())) {
                        this.deleteMessage(simpleReminderBot.getUserDataCache()
                                .getUserErrorMessage(callbackQuery.getMessage().getChatId()), simpleReminderBot);
                        simpleReminderBot.getUserDataCache()
                                .deleteUserErrorMessage(callbackQuery.getMessage().getChatId());
                    }
                    simpleReminderBot.execute(DeleteMessage.builder()
                            .messageId(callbackQuery.getMessage().getMessageId())
                            .chatId(callbackQuery.getMessage().getChatId())
                            .build()
                    );
                    simpleReminderBot.execute(SendMessage.builder().chatId(callbackQuery.getMessage().getChatId())
                            .text("Выбран месяц: " + Month.of(simpleReminderBot.getUserDataCache()
                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId()))
                                    .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))
                                    + "\nДля продолжения нажмите \"Далее\", либо выберите другой месяц."
                            )
                            .replyMarkup(reminderInlineKeyboards.getKeyboard("months"))
                            .build());
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

    private void deleteMessage(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        simpleReminderBot.execute(DeleteMessage.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .build()
        );
    }
}
