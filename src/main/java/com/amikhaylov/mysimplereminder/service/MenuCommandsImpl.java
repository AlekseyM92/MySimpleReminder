package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.config.YamlPropertiesConfig;
import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class MenuCommandsImpl implements MenuCommands {
    private static final List<BotCommand> listOfCommands = new ArrayList<>();
    private final YamlPropertiesConfig yamlPropertiesConfig;

    @Autowired
    public MenuCommandsImpl(YamlPropertiesConfig yamlPropertiesConfig) {
        this.yamlPropertiesConfig = yamlPropertiesConfig;
        yamlPropertiesConfig.getDescriptions().keySet()
                .forEach(k -> {
                    listOfCommands.add(new BotCommand(k, yamlPropertiesConfig.getDescriptions().get(k)));
                });
    }

    @Override
    public void initMenu(SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        simpleReminderBot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
    }

    public List<BotCommand> getListOfCommands() {
        return listOfCommands;
    }
}
