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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Component
@RequiredArgsConstructor
public class CallbackHandlerImpl implements CallbackHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    private static int currentMonth = LocalDate.now().getMonthValue();
    private static int currentYear = LocalDate.now().getYear();
    private final TextSaver textSaver;
    private final VoiceSaver voiceSaver;
    private final ReminderRepositoryService reminderRepositoryService;
    private final AnswerCallback answerCallback;
    private final AnswerMessage answerMessage;

    @Override
    public void handleCallback(CallbackQuery callbackQuery, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        if (callbackQuery == null) {
            throw new TelegramApiException("Callback query is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("SimpleReminderBot is null");
        }
        List<Reminder> reminders = new ArrayList<>();
        var callbackData = callbackQuery.getData();
        var chatId = callbackQuery.getMessage().getChatId();
        Message cancelMessage = new Message();
        List<Map<Long, Message>> tempMessages = new ArrayList<>();
        List<Map<Long, Message>> tempUserMessages = new ArrayList<>();
        if (callbackData != null) {
            AnswerCallbackQuery answerCallbackQuery;
            switch (callbackData) {
                case "cancel":
                    answerCallback.deleteRemindersMessages(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    simpleReminderBot.getUserDataCache().resetUserCache(chatId);
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot, "Отмена");
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Для создания напоминания выберите команду /create_reminder\n"
                                    + "Для вызова подсказки по управлению ботом выберите команду /help\n"
                                    + "Для вывода описания функционала бота выберите команду /description");
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
                            simpleReminderBot.getUserDataCache()
                                    .deleteReminderTextMessage(callbackQuery.getMessage().getChatId());
                        } else if (simpleReminderBot.getUserDataCache()
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
                            simpleReminderBot.getUserDataCache()
                                    .deleteReminderVoiceMessage(callbackQuery.getMessage().getChatId());
                        } else if (simpleReminderBot.getUserDataCache()
                                .getReminderTextMessage(callbackQuery.getMessage().getChatId()) == null
                                && simpleReminderBot.getUserDataCache()
                                .getReminderVoiceMessage(callbackQuery.getMessage().getChatId()) == null) {
                            LocalDate reminderDate = LocalDate
                                    .of(simpleReminderBot.getUserDataCache().getReminderYear(chatId)
                                            , Month.valueOf(simpleReminderBot.getUserDataCache()
                                                    .getUserChoiceOfMonth(chatId).toUpperCase())
                                            , Integer.parseInt(simpleReminderBot.getUserDataCache()
                                                    .getUserChoiceOfDay(chatId))
                                    );
                            Reminder editReminder = simpleReminderBot.getUserDataCache().getEditReminder(chatId);
                            if (editReminder != null) {
                                editReminder.setDateReminder(reminderDate);
                                editReminder.setSendDateTime(LocalDateTime.now());
                                reminderRepositoryService.saveReminder(editReminder);
                            }
                        }
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
                case "get_all_reminders":
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    simpleReminderBot.getUserDataCache().setUserState(callbackQuery.getMessage().getChatId()
                            , BotStatus.REVIEW_REMINDERS);
                    reminders = simpleReminderBot.getUserDataCache().getUserReminders(chatId);
                    tempMessages = new ArrayList<>();
                    var nr = 1;
                    for (Reminder reminder : reminders) {
                        Map<Long, Message> messages = new HashMap<>();
                        if (reminder.getFilePath().endsWith(".txt")) {
                            StringBuilder str1 = new StringBuilder();
                            try (BufferedReader bufferedReader
                                         = new BufferedReader(new FileReader(reminder.getFilePath()))) {
                                while (bufferedReader.ready()) {
                                    str1.append(bufferedReader.readLine());
                                }
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                            messages.put(reminder.getMessageId(), answerMessage.answerMessage(callbackQuery
                                    , simpleReminderBot
                                    , "Напоминание № " + nr + ":\n"
                                            + "Дата создания напоминания: "
                                            + reminder.getSendDateTime()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n"
                                            + "Дата напоминания: " + reminder.getDateReminder()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n"
                                            + "Текст напоминания:\n" + "\"" + str1 + "\""
                                    , reminderInlineKeyboards.getKeyboard("update_reminder")));
                            tempMessages.add(messages);
                            nr++;
                        } else if (reminder.getFilePath().endsWith(".ogg")) {
                            InputFile inputFile = new InputFile(new File(reminder.getFilePath()));
                            SendVoice sendVoice = SendVoice.builder()
                                    .voice(inputFile)
                                    .chatId(reminder.getChatId())
                                    .caption("Напоминание № " + nr + ":\n"
                                            + "Дата создания напоминания: "
                                            + reminder.getSendDateTime()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n"
                                            + "Дата напоминания: " + reminder.getDateReminder()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n")
                                    .replyMarkup(reminderInlineKeyboards.getKeyboard("update_reminder"))
                                    .disableNotification(true)
                                    .build();
                            messages.put(reminder.getMessageId(), simpleReminderBot.execute(sendVoice));
                            tempMessages.add(messages);
                            nr++;
                        }
                    }
                    Message message = answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Всего запланированных напоминаний: " + reminders.size() + "\n"
                                    + "Для выхода нажмите \"Отмена\""
                            , reminderInlineKeyboards.getKeyboard("cancel"));
                    tempMessages.add(Map.of(-1L, message));
                    simpleReminderBot.getUserDataCache().setTempUserMessages(chatId, tempMessages);
                    break;
                case "delete_reminder":
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    List<Map<Long, Message>> copyOfTempUserMessages = List.copyOf(simpleReminderBot.getUserDataCache()
                            .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                            .filter(t -> t.containsValue(callbackQuery.getMessage()))
                            .toList());
                    tempUserMessages = simpleReminderBot.getUserDataCache()
                            .getTempUserMessages(callbackQuery.getMessage().getChatId());
                    reminders = simpleReminderBot.getUserDataCache().getUserReminders(chatId);
                    if (!copyOfTempUserMessages.isEmpty() && !reminders.isEmpty()) {
                        for (Map<Long, Message> messages : copyOfTempUserMessages) {
                            for (Long reminder_id : messages.keySet()) {
                                for (Reminder reminder : reminders.stream()
                                        .filter(r -> Objects.equals(r.getMessageId(), reminder_id)).toList()) {
                                    File file = new File(reminder.getFilePath());
                                    if (file.exists()) {
                                        var isDeleted = file.delete();
                                        log.info("File " + reminder.getFilePath() + " is deleted: " + isDeleted);
                                    }
                                    reminderRepositoryService.deleteReminder(reminder);
                                    List<Reminder> copyOfReminders = new ArrayList<>(reminders);
                                    copyOfReminders.remove(reminder);
                                    cancelMessage = simpleReminderBot.getUserDataCache()
                                            .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                                            .filter(t -> t.containsKey(-1L))
                                            .toList().get(0).get(-1L);
                                    CallbackQuery callbackQuery1 = new CallbackQuery();
                                    callbackQuery1.setMessage(cancelMessage);
                                    answerCallback.deleteCallbackMessage(callbackQuery1, simpleReminderBot);
                                    Message message2 = answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                            , "Всего запланированных напоминаний: "
                                                    + copyOfReminders.size() + "\n"
                                                    + "Для выхода нажмите \"Отмена\""
                                            , reminderInlineKeyboards.getKeyboard("cancel"));
                                    simpleReminderBot.getUserDataCache().setUserReminders(chatId, copyOfReminders);
                                    tempMessages = new ArrayList<>();
                                    tempMessages.add(Map.of(-1L, message2));
                                    tempMessages.addAll(tempUserMessages);
                                    simpleReminderBot.getUserDataCache().setTempUserMessages(chatId
                                            , tempMessages.stream()
                                                    .filter(m -> !m.containsKey(reminder_id))
                                                    .toList()
                                    );
                                    tempUserMessages = simpleReminderBot.getUserDataCache()
                                            .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                                            .filter(t -> !t.containsKey(-1L))
                                            .toList();
                                    if (tempUserMessages.isEmpty() || tempUserMessages.get(0).isEmpty()) {
                                        cancelMessage = simpleReminderBot.getUserDataCache()
                                                .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                                                .filter(t -> t.containsKey(-1L))
                                                .toList().get(0).get(-1L);
                                        log.info("All reminders is deleted");
                                        answerCallback.deleteRemindersMessages(callbackQuery, simpleReminderBot);
                                        answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                                        simpleReminderBot.getUserDataCache().resetUserCache(chatId);
                                        callbackQuery.setMessage(cancelMessage);
                                        answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                                        answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                                , "Все напоминаня удалены!");
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "edit_reminder":
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.WAITING_FOR_CHOOSE_MONTH);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    cancelMessage = simpleReminderBot.getUserDataCache()
                            .getTempUserMessages(callbackQuery.getMessage().getChatId()).stream()
                            .filter(t -> t.containsKey(-1L))
                            .toList().get(0).get(-1L);
                    CallbackQuery callbackQuery1 = new CallbackQuery();
                    callbackQuery1.setMessage(cancelMessage);
                    reminders = simpleReminderBot.getUserDataCache().getUserReminders(chatId);
                    tempUserMessages = simpleReminderBot.getUserDataCache()
                            .getTempUserMessages(callbackQuery.getMessage().getChatId());
                    List<Map<Long, Message>> currentMap = tempUserMessages.stream()
                            .filter(m -> m.containsValue(callbackQuery.getMessage()))
                            .toList();
                    Long currentReminderId = currentMap.get(0).keySet().stream().toList().get(0);
                    answerCallback.deleteCallbackMessage(callbackQuery1, simpleReminderBot);
                    answerCallback.deleteRemindersMessages(callbackQuery, simpleReminderBot);
                    simpleReminderBot.getUserDataCache().deleteTempUserMessages(callbackQuery.getMessage().getChatId());
                    if (currentReminderId != null) {
                        Reminder currentReminder = reminders.stream()
                                .filter(r -> r.getMessageId().equals(currentReminderId))
                                .toList().get(0);
                        simpleReminderBot.getUserDataCache().setEditReminder(chatId, currentReminder);
                        reminderInlineKeyboards.refreshKeyboards();
                        answerMessage.answerMessage(callbackQuery, simpleReminderBot
                                , "Выберите месяц и нажмите \"Далее\""
                                , reminderInlineKeyboards.getKeyboard("months")
                        );
                    }
                    break;
                case "delete_all_reminders":
                    answerCallback.answerCallback(callbackQuery, simpleReminderBot);
                    answerCallback.deleteUserErrorMessageIfPresent(callbackQuery, simpleReminderBot);
                    answerCallback.deleteCallbackMessage(callbackQuery, simpleReminderBot);
                    reminders = simpleReminderBot.getUserDataCache().getUserReminders(chatId);
                    for (Reminder reminder : reminders) {
                        File file = new File(reminder.getFilePath());
                        if (file.exists()) {
                            var isDeleted = file.delete();
                            log.info("File " + reminder.getFilePath() + " is deleted: " + isDeleted);
                        }
                    }
                    reminderRepositoryService.deleteReminders(reminders);
                    answerMessage.answerMessage(callbackQuery, simpleReminderBot
                            , "Все напоминаня удалены!");
                    simpleReminderBot.getUserDataCache().resetUserCache(chatId);
                    break;
            }
        }
    }
}
