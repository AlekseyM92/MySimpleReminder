package com.amikhaylov.mysimplereminder.reminderhandlers;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import com.amikhaylov.mysimplereminder.service.AnswerCallback;
import com.amikhaylov.mysimplereminder.service.AnswerMessage;
import com.amikhaylov.mysimplereminder.service.TextSaver;
import com.amikhaylov.mysimplereminder.service.VoiceSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
    private final AnswerCallback answerCallback;
    private final AnswerMessage answerMessage;

    @Autowired
    public CallbackHandlerImpl(
            ReminderInlineKeyboards reminderInlineKeyboards,
            TextSaver textSaver,
            VoiceSaver voiceSaver,
            ReminderRepositoryService reminderRepositoryService,
            AnswerCallback answerCallback,
            AnswerMessage answerMessage) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
        this.textSaver = textSaver;
        this.voiceSaver = voiceSaver;
        this.reminderRepositoryService = reminderRepositoryService;
        this.answerCallback = answerCallback;
        this.answerMessage = answerMessage;
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
        var chatId = callbackQuery.getMessage().getChatId();
        if (callbackData != null) {
            AnswerCallbackQuery answerCallbackQuery;
            switch (callbackData) {
                case "cancel":
                    simpleReminderBot.getUserDataCache().resetUserCache(chatId);
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot, "Отмена");
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Для создания напоминания выберете команду /create_reminder.\n"
                                    + "Для вызова подсказки по управлению ботом выберете команду /help\n"
                                    + "Для вывода описания функционала бота выберете команду /description.");
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    break;
                case "next_step":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER) {
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                        simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                                , BotStatus.WAITING_FOR_CHOOSE_MONTH);
                        answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                        reminderInlineKeyboards.refreshKeyboards();
                        answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                        answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                , "Выберите месяц и нажмите \"Далее\""
                                , reminderInlineKeyboards.getKeyboard("months")
                        );
                    } else {
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot
                                , "Сначала отправьте сообщение,\n" +
                                        "затем нажмите \"Далее\"!"
                                , true
                        );
                    }
                    break;
                case "january", "february", "march", "april", "may", "june", "july"
                , "august", "september", "october", "november", "december":
                    simpleReminderBot.getUserDataCache().setUserChoiceOfMonth(callbackQuery.getMessage().getChatId()
                            , callbackData);
                    var selectedMonth = Month.valueOf(callbackData.toUpperCase()).getValue();
                    if (selectedMonth < currentMonth) {
                        currentYear++;
                    }
                    simpleReminderBot.getUserDataCache()
                            .setReminderYear(callbackQuery.getMessage().getChatId(), currentYear);
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.WAITING_FOR_APPLY_MONTH);
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Выбран год: " + currentYear +
                                    "\nВыбран месяц: " +
                                    Month.valueOf(simpleReminderBot
                                                    .getUserDataCache()
                                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                    .toUpperCase()
                                            )
                                            .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                                    "\nДля продолжения нажмите \"Далее\", либо выберите другой месяц."
                            , reminderInlineKeyboards.getKeyboard("months")
                    );
                    break;
                case "to_choose_days":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_MONTH) {
                        simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                                , BotStatus.WAITING_FOR_CHOOSE_DAY);
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                        answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                        answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                        answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                , "Выбран год: " + simpleReminderBot.getUserDataCache()
                                        .getReminderYear(callbackQuery.getMessage().getChatId()) +
                                        "\nВыбран месяц: " +
                                        Month.valueOf(simpleReminderBot
                                                        .getUserDataCache()
                                                        .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                                        .toUpperCase()
                                                )
                                                .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")) +
                                        "\nВыберите день и нажмите \"Готово\" для завершения создания напоминания."
                                , reminderInlineKeyboards.getKeyboard(
                                        simpleReminderBot.getUserDataCache()
                                                .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId())
                                )
                        );
                    } else {
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot
                                , "Сначала выберите месяц,\n" +
                                        "затем нажмите \"Далее\"!"
                                , true
                        );
                    }
                    break;
                case "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                     "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                     "22", "23", "24", "25", "26", "27", "28", "29", "30", "31":
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.WAITING_FOR_APPLY_DAY);
                    simpleReminderBot.getUserDataCache()
                            .setUserChoiceOfDay(callbackQuery.getMessage().getChatId(), callbackData);
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Выбран год: " + simpleReminderBot.getUserDataCache()
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
                            , reminderInlineKeyboards.getKeyboard(simpleReminderBot.getUserDataCache()
                                    .getUserChoiceOfMonth(callbackQuery.getMessage().getChatId()))
                    );
                    break;
                case "prev_step":
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.WAITING_FOR_CHOOSE_MONTH);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    reminderInlineKeyboards.refreshKeyboards();
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Выберите месяц и нажмите \"Далее\""
                            , reminderInlineKeyboards.getKeyboard("months"));
                    break;
                case "finish":
                    if (simpleReminderBot.getUserDataCache().getUserState(callbackQuery.getMessage().getChatId())
                            == BotStatus.WAITING_FOR_APPLY_DAY) {
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                        if (simpleReminderBot.getUserDataCache()
                                .getReminderTextMessage(callbackQuery.getMessage().getChatId()) != null) {
                            var resultFilePath = textSaver.saveTextFile(simpleReminderBot.getUserDataCache()
                                    .getReminderTextMessage(callbackQuery.getMessage().getChatId()));
                            if (resultFilePath != null) {
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
                        answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                        answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                        answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                , "Напоминание успешно создано!" + "\nНапоминание сработает " +
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
                                        " года."
                        );
                        simpleReminderBot.getUserDataCache().resetUserCache(chatId);
                    } else {
                        answerCallback.answerCallback(callbackQuery, simpleReminderBot
                                , "Сначала выберите день,\n" +
                                        "затем нажмите \"Готово\" для завершения создания напоминания!"
                                , true
                        );
                    }
                    break;
            }
        }
    }
}
