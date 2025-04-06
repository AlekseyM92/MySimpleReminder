package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.cache.BotStatus;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.keyboards.ReminderInlineKeyboards;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TextHandlerImpl implements TextHandler {
    private final ReminderInlineKeyboards reminderInlineKeyboards;
    @Autowired
    public TextHandlerImpl(ReminderInlineKeyboards reminderInlineKeyboards) {
        this.reminderInlineKeyboards = reminderInlineKeyboards;
    }
    @Override
    public void handleText(Chat chat, String text, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (chat == null) {
            throw new TelegramApiException("chat is null");
        } else if (text == null) {
            throw new TelegramApiException("text is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("simpleReminderBot is null");
        }
        SendMessage sendMessage = new SendMessage();
        Long chatId = chat.getId();
        sendMessage.setChatId(chatId);
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
                sendMessage.setText("Для создания напоминания отправьте текстовое или голосовое сообщение. " +
                        "Далее выберите месяц текущего года и число, когда напоминание должно сработать.");
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.CREATE_REMINDER);;
                sendMessage.setReplyMarkup(reminderInlineKeyboards
                        .getKeyboard("/create_reminder_1"));
                break;
            case "/my_reminders":
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.MY_REMINDERS);
                sendMessage.setText("Вы нажали /my_reminders");
                break;
            case "/delete_all_reminders":
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.DELETE_ALL_REMINDERS);
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
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.LANGUAGE);
                sendMessage.setText("Вы нажали /language");
                break;
            case "/terminate_bot":
                simpleReminderBot.getUserDataCache().setUserState(chatId, BotStatus.TERMINATE_BOT);
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
    }
}
