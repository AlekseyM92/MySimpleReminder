package com.amikhaylov.mysimplereminder.reminderhandlers;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import com.amikhaylov.mysimplereminder.database.service.RepositoryVoiceFile;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import com.amikhaylov.mysimplereminder.service.AnswerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log4j
@Component
@RequiredArgsConstructor
public class TextHandlerImpl implements TextHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    private final AnswerMessage answerMessage;
    private final RepositoryVoiceFile repositoryVoiceFile;
    private final ReminderRepositoryService reminderRepositoryService;

    @Override
    public void handleText(Message message, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (message == null) {
            throw new TelegramApiException("Message is null");
        } else if (message.getChat() == null) {
            throw new TelegramApiException("chat is null");
        } else if (message.getText() == null) {
            throw new TelegramApiException("text is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("simpleReminderBot is null");
        }
        Chat chat = message.getChat();
        Long chatId = chat.getId();
        String text = message.getText();
        List<Reminder> reminders = new ArrayList<>();
        if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.DEFAULT) {
            switch (text) {
                case "/start":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    answerMessage.answerMessage(message, simpleReminderBot
                            , "Привет " + chat.getFirstName() + "! \n"
                                    + "Для создания напоминания выберите команду /create_reminder.\n"
                                    + "Для вызова подсказки по управлению ботом выберите команду /help\n"
                                    + "Для вывода описания функционала бота выберите команду /description."
                    );

                    break;
                case "/create_reminder":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.CREATE_REMINDER);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    answerMessage.answerMessage(message, simpleReminderBot
                            , "Для создания напоминания отправьте текстовое или голосовое сообщение и нажмите" +
                                    " \"Далее\""
                            , reminderInlineKeyboards.getKeyboard("/create_reminder_1")
                    );
                    break;
                case "/my_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.MY_REMINDERS);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    reminders = reminderRepositoryService.findAllUserReminders(chatId)
                            .stream()
                            .filter(reminder -> !reminder.isDelivered())
                            .sorted(new Comparator<Reminder>() {
                                @Override
                                public int compare(Reminder o1, Reminder o2) {
                                    if (o1.getSendDateTime().isAfter(o2.getSendDateTime())) {
                                        return 1;
                                    } else if (o1.getSendDateTime().isBefore(o2.getSendDateTime())) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            })
                            .toList();
                    if (!reminders.isEmpty()) {
                        simpleReminderBot.getUserDataCache().setUserReminders(chatId, reminders);
                        answerMessage.answerMessage(message, simpleReminderBot
                                , "Сейчас будет выведен весь список запланированных напоминаний.\n" +
                                        "Вы соможете изменить дату напоминая либо удалить его.\n" +
                                        "Для продолжения нажмите \"Далее\" либо \"Отмена\" для выхода."
                                , reminderInlineKeyboards.getKeyboard("all_reminders"));
                    } else {
                        simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                        answerMessage.answerMessage(message, simpleReminderBot
                                , "У вас нет запланированных напоминаний!");
                    }
                    break;
                case "/delete_all_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DELETE_ALL_REMINDERS);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    reminders = reminderRepositoryService.findAllUserReminders(chatId)
                            .stream()
                            .filter(reminder -> !reminder.isDelivered())
                            .toList();
                    if (!reminders.isEmpty()) {
                        simpleReminderBot.getUserDataCache().setUserReminders(chatId, reminders);
                        answerMessage.answerMessage(message, simpleReminderBot
                                , "Все напоминания будут удалены, без возможности восстановления!\n" +
                                        "Хотите продолжить?"
                                , reminderInlineKeyboards.getKeyboard("delete_all_reminders"));
                    } else {
                        simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                        answerMessage.answerMessage(message, simpleReminderBot
                                , "У вас нет запланированных напоминаний!");
                    }
                    break;
                case "/help":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    answerMessage.answerMessage(message, simpleReminderBot
                            , "Для создания нового напоминания выберите команду /create_reminder" +
                                    " и следуйте дальнейшим инструкциям.\n" +
                                    "Для вывода всего списка напоминаний выберите команду /my_reminders.\n" +
                                    "Для удаления всех напоминаний выберите команду /delete_all_reminders.\n" +
                                    "Для вывода описания функционала бота выберите команду /description.");
                    break;
                case "/language":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    break;
                case "/terminate_bot":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    break;
                case "/description":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    answerMessage.answerMessage(message, simpleReminderBot
                            , "Бот, предоставляет возможность создать текстовое или" +
                                    " голосовое напоминание на ближайшие 6 месяцев.\n" +
                                    "Ограничения:\n" +
                                    "1. Длительность голосового сообщение не может превышать 2 минуты.\n" +
                                    "2. Количество символов в сообщении, включая символы пробела," +
                                    " не должно превышать 1000 штук.\n" +
                                    "3. Напоминание не может быть запланировано позднее чем " +
                                    "6 месяцев от текущей даты.\n" +
                                    "4. Точное время указать нельзя, только день.\n" +
                                    "5. Напоминание может сработать в любое время в течении назначенного дня." +
                                    " Рекомендуется дублировать напоминание на один день раньше ожидаемой" +
                                    " даты основного напоминания.\n" +
                                    "6. Запланированных напоминаний не может быть больше 50.\n" +
                                    "7. После блокировки бота все запланированные напоминания будут удалены," +
                                    " восстановить их возможности не будет.");
                    break;
                default:
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    answerMessage.deleteLastBotMessageIfPresent(message, simpleReminderBot);
                    answerMessage.answerMessage(message, simpleReminderBot, "Вы ввели "
                            + message.getText());
                    break;
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.CREATE_REMINDER) {
            if (text.isEmpty()) {
                answerMessage.sendErrorMessage(message, "Сообщение не может быть пустым!"
                        , simpleReminderBot);
            } else if (text.length() > 1000) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "В сообщении не может быть больше 1000 символов!", simpleReminderBot);
            } else if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка команд в режиме создания напоминания запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима.", simpleReminderBot);
            } else {
                answerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER);
                simpleReminderBot.getUserDataCache().setUserName(chatId, message.getChat().getFirstName());
                simpleReminderBot.getUserDataCache().setReminderTextMessage(chatId, message);
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId)
                == BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER
                || simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.WAITING_FOR_CHOOSE_DAY
                || simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.WAITING_FOR_CHOOSE_MONTH
                || simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.WAITING_FOR_APPLY_DAY
                || simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.WAITING_FOR_APPLY_MONTH) {
            if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка команд в режиме создания напоминания запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима. " +
                                "или нажмите \"Далее\" для продолжения", simpleReminderBot);
            } else {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Вы уже отправили сообщение!\nНажмите \"Далее\" для продолжения" +
                                " или \"Отмена\" для выхода из режима создания напоминания."
                        , simpleReminderBot);
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.MY_REMINDERS) {
            if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка команд в этом режиме запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима. " +
                                "или нажмите \"Далее\" для продолжения", simpleReminderBot);
            } else {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка сообщений в этом режиме запрещена!\nНажмите \"Далее\" для продолжения" +
                                " или \"Отмена\" для выхода из этого режима."
                        , simpleReminderBot);
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.DELETE_ALL_REMINDERS) {
            if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка команд в этом режиме запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима. " +
                                "или нажмите \"Удалить всё\" для завершения удаления", simpleReminderBot);
            } else {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка сообщений в этом режиме запрещена!\nНажмите \"Удалить всё\" " +
                                "для завершения удаления" +
                                " или \"Отмена\" для выхода из этого режима."
                        , simpleReminderBot);
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.REVIEW_REMINDERS) {
            if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка команд в этом режиме запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима.", simpleReminderBot);
            } else {
                answerMessage.deleteMessage(message, simpleReminderBot);
                answerMessage.sendErrorMessage(message
                        , "Отправка сообщений в этом режиме запрещена!\nНажмите \"Отмена\" " +
                                "для выхода из этого режима."
                        , simpleReminderBot);
            }
        }
    }
}
