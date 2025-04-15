package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.service.RepositoryVoiceFile;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Log4j
@Component
public class TextHandlerImpl implements TextHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    private final ReminderAnswerMessage reminderAnswerMessage;
    private final RepositoryVoiceFile repositoryVoiceFile;

    @Autowired
    public TextHandlerImpl(ReminderInlineKeyboards reminderInlineKeyboards
            , ReminderAnswerMessage reminderAnswerMessage
            , RepositoryVoiceFile repositoryVoiceFile) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
        this.reminderAnswerMessage = reminderAnswerMessage;
        this.repositoryVoiceFile = repositoryVoiceFile;
    }

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
        if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.DEFAULT) {
            switch (text) {
                case "/start":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot
                            , "Привет " + chat.getFirstName() + "! \n"
                                    + "Для создания напоминания выберете команду /create_reminder.\n"
                                    + "Для вызова подсказки по управлению ботом выберете команду /help\n"
                                    + "Для вывода описания функционала бота выберете команду /description."
                    );
                    break;
                case "/create_reminder":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.CREATE_REMINDER);
                    reminderAnswerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot
                            , "Для создания напоминания отправьте текстовое или голосовое сообщение и нажмите" +
                                    " \"Далее\""
                            , reminderInlineKeyboards.getKeyboard("/create_reminder_1")
                    );
                    break;
                case "/my_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot, "Вы нажали /my_reminders");

                    List<InputFile> inputFiles = repositoryVoiceFile.getVoicesByChatId(chatId);
                    if (!inputFiles.isEmpty()) {
                        reminderAnswerMessage.answerMessage(message, simpleReminderBot
                                , "Ваш список голосовых напоминаний:\n");
                        for (InputFile inputFile : inputFiles) {
                            log.info(inputFile.getAttachName());
                            SendVoice sendVoice = SendVoice.builder()
                                    .voice(new InputFile(new File(inputFile.getAttachName())))
                                    .chatId(message.getChatId())
                                    .build();
                            simpleReminderBot.execute(sendVoice);
                        }
                    } else {
                        reminderAnswerMessage.answerMessage(message, simpleReminderBot
                                , "У вас нет голосовых напоминаний!");
                    }
                    break;
                case "/delete_all_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot
                            , "Вы нажали /delete_all_reminders");
                    break;
                case "/help":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot
                            , "Для создания нового напоминания выберете команду /create_reminder" +
                                    " и следуйте дальнейшим иснтрукциям.\n" +
                                    "Для вывода всего списка напоминаний выберете команду /my_reminders.\n" +
                                    "Для удаления всех напоминаний выберете команду /delete_all_reminders.\n" +
                                    "Для вывода описания функционала бота выберете команду /description.\n" +
                                    "Для остановки бота выберете команду /terminate_bot, при этом все запланированные" +
                                    " напоминания будут удалены.");
                    break;
                case "/language":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot, "Вы нажали /language");
                    break;
                case "/terminate_bot":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot, "Вы нажали /terminate_bot");
                    break;
                case "/description":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot
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
                    reminderAnswerMessage.answerMessage(message, simpleReminderBot, "Вы ввели "
                            + message.getText());
                    break;
            }
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.CREATE_REMINDER) {
            if (text.isEmpty()) {
                reminderAnswerMessage.sendErrorMessage(message, "Сообщение не может быть пустым!"
                        , simpleReminderBot);
            } else if (text.length() > 1000) {
                reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
                reminderAnswerMessage.sendErrorMessage(message
                        , "В сообщении не может быть больше 1000 символов!", simpleReminderBot);
            } else if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
                reminderAnswerMessage.sendErrorMessage(message
                        , "Отправка команд в режиме создания напоминания запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима.", simpleReminderBot);
            } else {
                reminderAnswerMessage.deleteUserErrorMessageIfPresent(message, simpleReminderBot);
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
                reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
                reminderAnswerMessage.sendErrorMessage(message
                        , "Отправка команд в режиме создания напоминания запрещена!" +
                                " Нажмите \"Отмена\" для выхода из данного режима. " +
                                "или нажмите \"Далее\" для продолжения", simpleReminderBot);
            } else {
                reminderAnswerMessage.deleteMessage(message, simpleReminderBot);
                reminderAnswerMessage.sendErrorMessage(message
                        , "Вы уже отправили сообщение!\nНажмите \"Далее\" для продолжения" +
                                " или \"Отмена\" для выхода из режима создания напоминания."
                        , simpleReminderBot);
            }
        }
    }
}
