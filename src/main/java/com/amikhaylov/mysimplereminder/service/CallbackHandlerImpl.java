package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class CallbackHandlerImpl implements CallbackHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    private static int currentMonth = LocalDate.now().getMonthValue();
    private static int currentYear = LocalDate.now().getYear();
    private final TextSaver textSaver;
    private final VoiceSaver voiceSaver;
    private final ReminderRepositoryService reminderRepositoryService;

    @Autowired
    public CallbackHandlerImpl(
            ReminderInlineKeyboards reminderInlineKeyboards,
            TextSaver textSaver,
            VoiceSaver voiceSaver,
            ReminderRepositoryService reminderRepositoryService) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
        this.textSaver = textSaver;
        this.voiceSaver = voiceSaver;
        this.reminderRepositoryService = reminderRepositoryService;
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
                    simpleReminderBot.getUserDataCache()
                            .deleteUserChoiceOfDay(callbackQuery.getMessage().getChatId());
                    simpleReminderBot.getUserDataCache()
                            .deleteUserChoiceOfMonth(callbackQuery.getMessage().getChatId());
                    simpleReminderBot.getUserDataCache()
                            .deleteReminderTextMessage(callbackQuery.getMessage().getChatId());
                    simpleReminderBot.getUserDataCache()
                            .deleteReminderVoiceMessage(callbackQuery.getMessage().getChatId());
                    simpleReminderBot.getUserDataCache()
                            .deleteReminderYear(callbackQuery.getMessage().getChatId());
                    simpleReminderBot.getUserDataCache()
                            .deleteUserName(callbackQuery.getMessage().getChatId());
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
                                .build()
                        );
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
                            , callbackData);
                    var selectedMonth = Month.valueOf(callbackData.toUpperCase()).getValue();
                    if (selectedMonth < currentMonth) {
                        currentYear = currentYear + 1;
                    }
                    simpleReminderBot.getUserDataCache()
                            .setReminderYear(callbackQuery.getMessage().getChatId(), currentYear);
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
                            .text("Выбран год: " + currentYear +
                                    "\nВыбран месяц: " +
                                    Month.valueOf(simpleReminderBot
                                                    .getUserDataCache()
                                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                    .toUpperCase()
                                            )
                                            .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                                    "\nДля продолжения нажмите \"Далее\", либо выберите другой месяц."
                            )
                            .replyMarkup(reminderInlineKeyboards.getKeyboard("months"))
                            .build());
                    break;
                case "to_choose_days":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_MONTH) {
                        simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                                , BotStatus.WAITING_FOR_CHOOSE_DAY);
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
                                .text("Выбран год: " + simpleReminderBot.getUserDataCache()
                                        .getReminderYear(callbackQuery.getMessage().getChatId()) +
                                        "\nВыбран месяц: " +
                                        Month.valueOf(simpleReminderBot
                                                        .getUserDataCache()
                                                        .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                        .toUpperCase()
                                                )
                                                .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                                        "\nВыберите день и нажмите \"Готово\" для завершения создания напоминания.")
                                .replyMarkup(reminderInlineKeyboards.getKeyboard(
                                        simpleReminderBot.getUserDataCache()
                                                .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId()))
                                )
                                .build());
                    } else {
                        answerCallbackQuery = AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQuery.getId())
                                .showAlert(true)
                                .text("Сначала выберите месяц,\n" +
                                        "затем нажмите \"Далее\"!")
                                .build();
                        simpleReminderBot.execute(answerCallbackQuery);
                    }
                    break;
                case "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                     "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                     "22", "23", "24", "25", "26", "27", "28", "29", "30", "31":
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.WAITING_FOR_APPLY_DAY);
                    simpleReminderBot.getUserDataCache()
                            .setUserChoiceOfDay(callbackQuery.getMessage().getChatId(), callbackData);
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
                            .text("Выбран год: " + simpleReminderBot.getUserDataCache()
                                    .getReminderYear(callbackQuery.getMessage().getChatId()) +
                                    "\nВыбран месяц: " +
                                    Month.valueOf(simpleReminderBot
                                                    .getUserDataCache()
                                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                    .toUpperCase()
                                            )
                                            .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                                    "\nВыбрано число месяца: " + simpleReminderBot.getUserDataCache()
                                    .getUserChoiceOfDay(callbackQuery.getMessage().getChatId()) +
                                    "\nДля завершения создания напоминания нажмите \"Готово\"," +
                                    "\nлибо выберите другое число."
                            )
                            .replyMarkup(reminderInlineKeyboards.getKeyboard(simpleReminderBot.getUserDataCache()
                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())))
                            .build());
                    break;
                case "prev_step":
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
                    break;
                case "finish":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_DAY) {
                        answerCallbackQuery = AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQuery.getId())
                                .build();
                        simpleReminderBot.execute(answerCallbackQuery);
                        if (simpleReminderBot.getUserDataCache()
                                .getReminderTextMessage(callbackQuery.getMessage().getChatId()) != null) {
                            var resultFilePath = textSaver.saveTextFile(simpleReminderBot.getUserDataCache()
                                    .getReminderTextMessage(callbackQuery.getMessage().getChatId()));
                            if (resultFilePath != null) {
                                var chatId = callbackQuery.getMessage().getChatId();
                                LocalDate reminderDate = LocalDate
                                        .of(simpleReminderBot.getUserDataCache().getReminderYear(chatId)
                                                , Month.valueOf(simpleReminderBot.getUserDataCache()
                                                        .getUserChoiceOfMonth(chatId).toUpperCase())
                                                , Integer.parseInt(simpleReminderBot.getUserDataCache()
                                                        .getUserChoiceOfDay(chatId))
                                        );
                                reminderRepositoryService.saveReminder(new Reminder(chatId
                                        , simpleReminderBot.getUserDataCache().getUserName(chatId)
                                        , LocalDateTime.now(), reminderDate, resultFilePath, false));
                            }
                        }
                        simpleReminderBot.getUserDataCache()
                                .deleteReminderTextMessage(callbackQuery.getMessage().getChatId());
                        if (simpleReminderBot.getUserDataCache()
                                .getReminderVoiceMessage(callbackQuery.getMessage().getChatId()) != null) {
                            var resultFilePath = voiceSaver
                                    .downloadAndSaveVoiceFile(simpleReminderBot.getUserDataCache()
                                                    .getReminderVoiceMessage(callbackQuery.getMessage().getChatId())
                                            , simpleReminderBot);
                            if (resultFilePath != null) {
                                var chatId = callbackQuery.getMessage().getChatId();
                                LocalDate reminderDate = LocalDate
                                        .of(simpleReminderBot.getUserDataCache().getReminderYear(chatId)
                                                , Month.valueOf(simpleReminderBot.getUserDataCache()
                                                        .getUserChoiceOfMonth(chatId).toUpperCase())
                                                , Integer.parseInt(simpleReminderBot.getUserDataCache()
                                                        .getUserChoiceOfDay(chatId))
                                        );
                                reminderRepositoryService.saveReminder(new Reminder(chatId
                                        , simpleReminderBot.getUserDataCache().getUserName(chatId)
                                        , LocalDateTime.now(), reminderDate, resultFilePath, false));
                            }
                        }
                        simpleReminderBot.getUserDataCache()
                                .deleteReminderVoiceMessage(callbackQuery.getMessage().getChatId());
                        simpleReminderBot.getUserDataCache()
                                .setUserState(callbackQuery.getMessage().getChatId(), BotStatus.DEFAULT);
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
                                .text("Напоминание успешно создано!" + "\nНапоминание сработает " +
                                        simpleReminderBot.getUserDataCache()
                                                .getUserChoiceOfDay(callbackQuery.getMessage().getChatId()) +
                                        " " +
                                        Month.valueOf(simpleReminderBot
                                                        .getUserDataCache()
                                                        .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                        .toUpperCase()
                                                )
                                                .getDisplayName(TextStyle.FULL, new Locale("ru")) +
                                        " " +
                                        simpleReminderBot.getUserDataCache()
                                                .getReminderYear(callbackQuery.getMessage().getChatId()) +
                                        " года.")
                                .build()
                        );
                        simpleReminderBot.getUserDataCache()
                                .deleteUserChoiceOfDay(callbackQuery.getMessage().getChatId());
                        simpleReminderBot.getUserDataCache()
                                .deleteUserChoiceOfMonth(callbackQuery.getMessage().getChatId());
                        simpleReminderBot.getUserDataCache()
                                .deleteReminderYear(callbackQuery.getMessage().getChatId());
                        simpleReminderBot.getUserDataCache()
                                .deleteUserName(callbackQuery.getMessage().getChatId());
                    } else {
                        answerCallbackQuery = AnswerCallbackQuery.builder()
                                .callbackQueryId(callbackQuery.getId())
                                .showAlert(true)
                                .text("Сначала выберите день,\n" +
                                        "затем нажмите \"Готово\" для завершения создания напоминания!")
                                .build();
                        simpleReminderBot.execute(answerCallbackQuery);
                    }
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
