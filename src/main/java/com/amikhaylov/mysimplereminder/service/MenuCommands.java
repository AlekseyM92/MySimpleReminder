package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public interface MenuCommands {
    public void initMenu(SimpleReminderBot simpleReminderBot) throws TelegramApiException;
    public List<BotCommand> getListOfCommands();
}
