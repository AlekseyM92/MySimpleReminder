package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Log4j
@Component
public class TextHandlerImpl implements TextHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;

    @Autowired
    public TextHandlerImpl(ReminderInlineKeyboards reminderInlineKeyboards) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
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
        SendMessage sendMessage = new SendMessage();
        Chat chat = message.getChat();
        Long chatId = chat.getId();
        String text = message.getText();
        sendMessage.setChatId(chatId);
        if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.DEFAULT) {
            switch (text) {
                case "/start":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    sendMessage.setText("Привет " + chat.getFirstName() + "! \n"
                            + "Для создания напоминания выберете команду /create_reminder.\n"
                            + "Для вызова подсказки по управлению ботом выберете команду /help\n"
                            + "Для вывода описания функционала бота выберете команду /description."
                    );
                    break;
                case "/create_reminder":
                    if (simpleReminderBot.getUserDataCache().errorMessageIsPresent(message.getChatId())) {
                        simpleReminderBot.getUserDataCache().deleteUserErrorMessage(message.getChatId());
                    }
                    sendMessage.setText("Для создания напоминания отправьте текстовое или голосовое сообщение и нажмите" +
                            " \"Далее\"");
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.CREATE_REMINDER);
                    ;
                    sendMessage.setReplyMarkup(reminderInlineKeyboards
                            .getKeyboard("/create_reminder_1"));
                    break;
                case "/my_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    //simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.MY_REMINDERS);
                    sendMessage.setText("Вы нажали /my_reminders");
                    break;
                case "/delete_all_reminders":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    //simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DELETE_ALL_REMINDERS);
                    sendMessage.setText("Вы нажали /delete_all_reminders");
                    break;
                case "/help":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    sendMessage.setText("Для создания нового напоминания выберете команду /create_reminder" +
                            " и следуйте дальнейшим иснтрукциям.\n" +
                            "Для вывода всего списка напоминаний выберете команду /my_reminders.\n" +
                            "Для удаления всех напоминаний выберете команду /delete_all_reminders.\n" +
                            "Для вывода описания функционала бота выберете команду /description.\n" +
                            "Для остановки бота выберете команду /terminate_bot, при этом все запланированные" +
                            " напоминания будут удалены.");
                    break;
                case "/language":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    //simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.LANGUAGE);
                    sendMessage.setText("Вы нажали /language");
                    break;
                case "/terminate_bot":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    //simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.TERMINATE_BOT);
                    sendMessage.setText("Вы нажали /terminate_bot");
                    break;
                case "/description":
                    simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DEFAULT);
                    sendMessage.setText("Бот, предоставляет возможность создать текстовое или" +
                            " голосовое напоминание на ближайшие 6 месяцев.\n" +
                            "Ограничения:\n" +
                            "1. Длительность голосового сообщение не может превышать 2 минуты.\n" +
                            "2. Количество символов в сообщении, включая символы пробела," +
                            " не должно превышать 1000 штук.\n" +
                            "3. Напоминание не может быть запланировано позднее чем 6 месяцев от текущей даты.\n" +
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
                    sendMessage.setText("Вы ввели " + text);
                    break;
            }
            simpleReminderBot.execute(sendMessage);
        } else if (simpleReminderBot.getUserDataCache().getUserState(chatId) == BotStatus.CREATE_REMINDER) {
            if (text.isEmpty()) {
                sendErrorMessage(message, "Сообщение не может быть пустым!", simpleReminderBot);
            } else if (text.length() > 1000) {
                deleteMessage(message, simpleReminderBot);
                sendErrorMessage(message, "В сообщении не может быть больше 1000 символов!"
                        , simpleReminderBot);
            } else if (simpleReminderBot.getMenuCommands()
                    .getListOfCommands().stream()
                    .map(BotCommand::getCommand).toList().contains(text.substring(1))) {
                deleteMessage(message, simpleReminderBot);
                sendErrorMessage(message, "Отправка команд в режиме создания напоминания запрещена!" +
                        " Нажмите \"Отмена\" для выхода из данного режима.", simpleReminderBot);
            } else {
                if (simpleReminderBot.getUserDataCache()
                        .errorMessageIsPresent(message.getChatId())) {
                    this.deleteMessage(simpleReminderBot.getUserDataCache()
                            .getUserErrorMessage(message.getChatId()), simpleReminderBot);
                    simpleReminderBot.getUserDataCache()
                            .deleteUserErrorMessage(message.getChatId());
                }
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.WAITING_FOR_APPLY_MESSAGE_REMINDER);
                simpleReminderBot.getUserDataCache().setReminderTextMessage(message.getChatId(), message);
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
                deleteMessage(message, simpleReminderBot);
                sendErrorMessage(message, "Отправка команд в режиме создания напоминания запрещена!" +
                        " Нажмите \"Отмена\" для выхода из данного режима. " +
                        "или нажмите \"Далее\" для продолжения", simpleReminderBot);
            } else {
                deleteMessage(message, simpleReminderBot);
                sendErrorMessage(message, "Вы уже отправили сообщение!\nНажмите \"Далее\" для продолжения" +
                                " или \"Отмена\" для выхода из режима создания напоминания."
                        , simpleReminderBot);
            }
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
        DeleteMessages deleteMessages = new DeleteMessages();
        deleteMessages.setChatId(chatId);
        deleteMessages.setMessageIds(messageIds);
        simpleReminderBot.execute(deleteMessages);
    }
}
